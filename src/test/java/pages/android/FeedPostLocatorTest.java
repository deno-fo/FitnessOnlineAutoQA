package pages.android;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import utils.AndroidConfig;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeedPostLocatorTest {
    @Test
    void selectsOwnActionsAndCountsAfterAnotherPostIsInsertedAbove() throws Exception {
        Document document = document();
        appendPost(document, "other", true);
        appendPost(document, "ours", true);
        for (String control : new String[]{
                "iconLike", "iconDislike", "iconComments",
                "labelLikesCount", "labelDislikesCount"
        }) {
            NodeList matches = find(document, "ours", control);
            assertEquals(1, matches.getLength());
            assertEquals("ours", ((Element) matches.item(0)).getAttribute("owner"));
        }
    }

    @Test
    void doesNotBorrowControlsFromNeighbourWhenOwnCardIsIncomplete() throws Exception {
        Document document = document();
        appendPost(document, "other", true);
        appendPost(document, "ours", false);
        assertEquals(0, find(document, "ours", "iconLike").getLength());
    }

    @Test
    void missingPostDoesNotMatchAnyActions() throws Exception {
        Document document = document();
        appendPost(document, "other", true);
        assertEquals(0, find(document, "ours", "iconLike").getLength());
    }

    @Test
    void supportsPostTextContainingBothQuoteTypes() throws Exception {
        Document document = document();
        String text = "User's \"quoted\" post";
        appendPost(document, text, true);
        assertEquals(1, find(document, text, "iconComments").getLength());
    }

    private Document document() throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        document.appendChild(document.createElement("feed"));
        return document;
    }

    private void appendPost(Document document, String text, boolean complete) {
        Element card = document.createElement("card");
        document.getDocumentElement().appendChild(card);
        Element content = document.createElement("content");
        card.appendChild(content);
        Element body = element(document, "bodyText", text);
        body.setAttribute("text", text);
        content.appendChild(body);
        Element actions = document.createElement("actions");
        card.appendChild(actions);
        for (String control : new String[]{
                "iconLike", "iconDislike", "iconComments",
                "labelLikesCount", "labelDislikesCount"
        }) {
            if (complete || !control.equals("iconComments")) {
                actions.appendChild(element(document, control, text));
            }
        }
    }

    private Element element(Document document, String id, String owner) {
        Element element = document.createElement("node");
        element.setAttribute("resource-id", AndroidConfig.APP_PACKAGE + ":id/" + id);
        element.setAttribute("owner", owner);
        return element;
    }

    private NodeList find(Document document, String text, String control) throws Exception {
        return (NodeList) XPathFactory.newInstance().newXPath().evaluate(
                FeedPage.postControlXPath(text, control), document, XPathConstants.NODESET
        );
    }
}
