package components.ios;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordManagerPromptLocatorTest {

    @Test
    void findsNotNowOnlyInsideSavePasswordSheet() throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
        Element root = document.createElement("Application");
        document.appendChild(root);

        Element unrelated = document.createElement("XCUIElementTypeOther");
        unrelated.setAttribute("name", "Not Now");
        root.appendChild(unrelated);

        Element sheet = document.createElement("XCUIElementTypeSheet");
        sheet.setAttribute("name", "Save Password?");
        root.appendChild(sheet);

        Element button = document.createElement("XCUIElementTypeOther");
        button.setAttribute("label", "Not Now");
        sheet.appendChild(button);

        NodeList matches = (NodeList) XPathFactory.newInstance()
                .newXPath()
                .evaluate(
                        IosPasswordManagerPrompt.NOT_NOW_BUTTON_XPATH,
                        document,
                        XPathConstants.NODESET
                );

        assertEquals(1, matches.getLength());
        assertEquals(
                "Not Now",
                ((Element) matches.item(0)).getAttribute("label")
        );
    }
}
