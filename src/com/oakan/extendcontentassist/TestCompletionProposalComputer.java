package com.oakan.extendcontentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.contentassist.ICompletionProposalComputer;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TestCompletionProposalComputer implements ICompletionProposalComputer {

	private String[]	FRUIT_NAMES	= { "Apple", "Apricot", "Banana", "Breadfruit", "Blackberry", "Blackcurrant", "Blueberry", "Currant", "Cherry", "Cloudberry", "Coconut", "Date", "Dragonfruit",
			"Durian", "Fig", "Gooseberry", "Grape", "Grapefruit", "Guava" };

	@Override
	public List<ICompletionProposal> computeCompletionProposals(CompletionProposalInvocationContext context, IProgressMonitor monitor) {

		Node selectedNode = (Node) ContentAssistUtils.getNodeAt(context.getViewer(), context.getInvocationOffset());

		Element tag = null;
		if (selectedNode instanceof Element) {
			tag = (Element) selectedNode;
		} else if (selectedNode instanceof Text && selectedNode.getParentNode() instanceof Element) {
			tag = (Element) selectedNode.getParentNode();
		}

		// Process only <fruit> tags
		if (tag == null || !"fruit".equals(tag.getLocalName())) {
			return Collections.emptyList();
		}

		// Determine text node
		IDOMNode textNode = null;
		if (selectedNode instanceof Text) {
			textNode = (IDOMNode) selectedNode;
		} else {
			if (selectedNode.getChildNodes().getLength() == 1 && selectedNode.getChildNodes().item(0) instanceof Text) {
				// Cursor at the end of a text node
				textNode = (IDOMNode) selectedNode.getChildNodes().item(0);
			} else {
				// Cursor between two tags, no text node yet
			}
		}

		// Determine selected text and offsets
		String selectedText = null;
		int textNodeOffset = -1;
		int cursorOffsetWithinTextNode = -1;

		if (textNode != null) {
			selectedText = textNode.getStartStructuredDocumentRegion().getText();
			textNodeOffset = textNode.getStartOffset();
			cursorOffsetWithinTextNode = context.getInvocationOffset() - textNodeOffset;
		} else {
			selectedText = "";
			textNodeOffset = context.getInvocationOffset();
			cursorOffsetWithinTextNode = 0;
		}

		// Gather proposals
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String searchPrefix = selectedText.substring(0, cursorOffsetWithinTextNode);

		for (String searchResult : findFruits(searchPrefix)) {
			proposals.add(new CompletionProposal(searchResult, textNodeOffset, selectedText.length(), searchResult.length()));
		}

		return proposals;
	}

	private List<String> findFruits(String searchPrefix) {
		List<String> result = new ArrayList<String>();

		for (String fruitName : FRUIT_NAMES) {
			if (fruitName.startsWith(searchPrefix)) {
				result.add(fruitName);
			}
		}

		return result;
	}

	@Override
	public List<ICompletionProposal> computeContextInformation(CompletionProposalInvocationContext context, IProgressMonitor monitor) {
		return Collections.emptyList();
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {
	}

	@Override
	public void sessionStarted() {
	}

}
