#!/usr/bin/env bash

set -uo pipefail

TEST_SELECTOR="${1:-}"
SYSTEM_PORT_BASE="${ANDROID_SYSTEM_PORT_BASE:-8200}"
MATRIX_ROOT="${PWD}/target/android-matrix"

if [[ ! -x "./mvnw" ]]; then
    echo "Ошибка: файл ./mvnw не найден или не является исполняемым."
    echo "Запусти команду из корня проекта."
    exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
    echo "Ошибка: команда adb не найдена."
    exit 1
fi

devices=()

while IFS= read -r udid; do
    if [[ -n "$udid" ]]; then
        devices+=("$udid")
    fi
done < <(
    adb devices \
        | awk 'NR > 1 && $2 == "device" {print $1}' \
        | sort
)

if [[ "${#devices[@]}" -eq 0 ]]; then
    echo "Ошибка: не найдено ни одного авторизованного Android-устройства."
    echo
    adb devices
    exit 1
fi

mkdir -p "$MATRIX_ROOT"

echo "Найдено Android-устройств: ${#devices[@]}"

if [[ -n "$TEST_SELECTOR" ]]; then
    echo "Запускаемый тест: $TEST_SELECTOR"
else
    echo "Запускается весь Android-набор тестов."
fi

echo

pids=()
device_names=()
log_files=()

for index in "${!devices[@]}"; do
    udid="${devices[$index]}"
    system_port=$((SYSTEM_PORT_BASE + index))

    model="$(
        adb -s "$udid" shell getprop ro.product.model 2>/dev/null \
            | tr -d '\r' \
            | xargs
    )"

    if [[ -z "$model" ]]; then
        model="Android"
    fi

    safe_name="$(
        printf '%s_%s' "$model" "$udid" \
            | tr ' /:' '___' \
            | tr -cd '[:alnum:]_.-'
    )"

    if [[ -z "$safe_name" ]]; then
        safe_name="device_${index}"
    fi

    run_directory="$MATRIX_ROOT/$safe_name"
    build_directory="$run_directory/build"
    log_file="$run_directory/run.log"

    mkdir -p "$run_directory"

    command=(
        ./mvnw
        -Pandroid
        "-Dqa.build.directory=$build_directory"
        "-Dandroid.udid=$udid"
        "-Dandroid.systemPort=$system_port"
    )

    if [[ -n "$TEST_SELECTOR" ]]; then
        command+=("-Dtest=$TEST_SELECTOR")
    fi

    command+=(test)

    echo "START: $model"
    echo "       UDID: $udid"
    echo "       systemPort: $system_port"
    echo "       log: $log_file"
    echo

    "${command[@]}" >"$log_file" 2>&1 &

    pids+=("$!")
    device_names+=("$model [$udid]")
    log_files+=("$log_file")
done

failures=0

for index in "${!pids[@]}"; do
    pid="${pids[$index]}"
    device_name="${device_names[$index]}"
    log_file="${log_files[$index]}"

    if wait "$pid"; then
        echo "PASS: $device_name"
    else
        echo "FAIL: $device_name"
        echo "      Лог: $log_file"
        failures=$((failures + 1))
    fi
done

echo

if [[ "$failures" -gt 0 ]]; then
    echo "Android matrix завершилась с ошибками: $failures"
    exit 1
fi

echo "Android matrix завершилась успешно на всех устройствах."