package sofwareEngineeringProject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.mar9000.antmark.grammar.*;
import org.mar9000.antmark.grammar.MarkdownParser.*;

import sample.MarkdownLexer;
import sample.MarkdownParser;
import sample.MarkdownParser.HtmlBlockInTagsContext;
import sample.MarkdownParser.HtmlBlockTagsContext;
import sample.MarkdownParser.HtmlCommentContext;
import sample.MarkdownParser.InlineListItemContext;
import sample.MarkdownParser.ListItemBlankLineContext;
import sample.MarkdownParser.RawLineContext;
import sample.MarkdownParserBaseListener;

public class MarkdownTranslator extends MarkdownParserBaseListener {
	
	private StringBuffer html = new StringBuffer();
	/* Default new line. When a paragraph is closed with \r \n we should use this.   */
	private String nextNewline = "\n";
	private boolean withReference = false;
	private int listIndentSpaces = 2;
	private boolean indentList = false;
	private int listLevel = 0;
	private final String INDENT_SPACES = "                              ";
	// Needed to calculate references.
	private ParseTree tree;
	private MarkdownParser parser;
	private HashMap<String, MarkdownParser.ReferenceContext> references =
			new HashMap<String, MarkdownParser.ReferenceContext>();
	
	public MarkdownTranslator(ParseTree tree, MarkdownParser parser) {
		this.tree = tree;
		this.parser = parser;
		// Collect reference to be used for links and images.
		for (ParseTree t: XPath.findAll(tree, "//reference", parser) ) {
			MarkdownParser.ReferenceContext reference = (MarkdownParser.ReferenceContext)t;
			references.put(reference.referenceLabel().referenceId().getText().toLowerCase(), reference);
		}
	}
	
	public static String translate(String source) throws IllegalArgumentException {
		ANTLRInputStream input = new ANTLRInputStream(source);
		MarkdownLexer lexer = new MarkdownLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MarkdownParser parser = new MarkdownParser(tokens);
		ParseTree tree = parser.document();
		if (parser.getNumberOfSyntaxErrors() > 0) {
			throw new IllegalArgumentException("Syntax error in blockquote " + source);
		}
		//
		ParseTreeWalker walker = new ParseTreeWalker();
		MarkdownTranslator translator = new MarkdownTranslator(tree, parser);
		walker.walk(translator, tree);
		if (translator.isWithReferences()) {
			translator.clearHtml();
			walker.walk(translator, tree);
		}
		return translator.getHtml();
	}
	
	public static String escapeHTML(String html) {
		StringBuffer sb = new StringBuffer();
		int n = html.length();
		for (int i = 0; i < n; i++) {
			char c = html.charAt(i);
			switch (c) {
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '&': sb.append("&amp;"); break;
			case '"': sb.append("&quot;"); break;
			case '\'': sb.append("&#39;"); break;
			case 'Ã ': sb.append("&agrave;");break;
			case 'Ã€': sb.append("&Agrave;");break;
			case 'Ã¢': sb.append("&acirc;");break;
			case 'Ã‚': sb.append("&Acirc;");break;
			case 'Ã¤': sb.append("&auml;");break;
			case 'Ã„': sb.append("&Auml;");break;
			case 'Ã¥': sb.append("&aring;");break;
			case 'Ã…': sb.append("&Aring;");break;
			case 'Ã¦': sb.append("&aelig;");break;
			case 'Ã†': sb.append("&AElig;");break;
			case 'Ã§': sb.append("&ccedil;");break;
			case 'Ã‡': sb.append("&Ccedil;");break;
			case 'Ã©': sb.append("&eacute;");break;
			case 'Ã‰': sb.append("&Eacute;");break;
			case 'Ã¨': sb.append("&egrave;");break;
			case 'Ãˆ': sb.append("&Egrave;");break;
			case 'Ãª': sb.append("&ecirc;");break;
			case 'ÃŠ': sb.append("&Ecirc;");break;
			case 'Ã«': sb.append("&euml;");break;
			case 'Ã‹': sb.append("&Euml;");break;
			case 'Ã¯': sb.append("&iuml;");break;
			case 'Ã�': sb.append("&Iuml;");break;
			case 'Ã´': sb.append("&ocirc;");break;
			case 'Ã”': sb.append("&Ocirc;");break;
			case 'Ã¶': sb.append("&ouml;");break;
			case 'Ã–': sb.append("&Ouml;");break;
			case 'Ã¸': sb.append("&oslash;");break;
			case 'Ã˜': sb.append("&Oslash;");break;
			case 'ÃŸ': sb.append("&szlig;");break;
			case 'Ã¹': sb.append("&ugrave;");break;
			case 'Ã™': sb.append("&Ugrave;");break;         
			case 'Ã»': sb.append("&ucirc;");break;         
			case 'Ã›': sb.append("&Ucirc;");break;
			case 'Ã¼': sb.append("&uuml;");break;
			case 'Ãœ': sb.append("&Uuml;");break;
			case 'Â®': sb.append("&reg;");break;         
			case 'Â©': sb.append("&copy;");break;   
			case 'â‚¬': sb.append("&euro;"); break;
			//
			default:  sb.append(c); break;
			}
		}
		return sb.toString();
	}
	
	public String getHtml() {
		return html.toString();
	}
	public void clearHtml() {
		html = new StringBuffer();
	}
	
	public boolean isWithReferences() {
		return withReference;
	}
	
	private static String removeFirstAndLast(String s) {
		if (s == null || s.length() == 1)
			return s;
		if (s.length() == 2)
			return "";
		return s.substring(1, s.length()-1);
	}

	@Override public void enterBlock(@NotNull MarkdownParser.BlockContext ctx) {
		if (html.length() > 0 && ctx.references() == null) {
			html.append(nextNewline).append(nextNewline);
			nextNewline = "\n";
		}
		if (ctx.htmlBlockSelfClosing() != null) {
			html.append(ctx.htmlBlockSelfClosing().getText());
		}
	}
	
	@Override public void exitBlock(@NotNull MarkdownParser.BlockContext ctx) {
		//if (ctx.references() == null) { // TODO: remove, not required we set \n entering block.
		//	html.append("\n\n");
		//}
	}
	
	@Override
	public void enterSetextHeading1(@NotNull MarkdownParser.SetextHeading1Context ctx) {
		html.append("<h1>");
	}

	@Override
	public void exitSetextHeading1(@NotNull MarkdownParser.SetextHeading1Context ctx) {
		html.append("</h1>");
	}

	@Override public void enterSetextHeading2(@NotNull MarkdownParser.SetextHeading2Context ctx) {
		html.append("<h2>");
	}

	@Override public void exitSetextHeading2(@NotNull MarkdownParser.SetextHeading2Context ctx) {
		html.append("</h2>");
	}

	@Override public void enterAtxHeading(@NotNull MarkdownParser.AtxHeadingContext ctx) {
		String tag = "h" + ctx.ATX_START().getText().length();
		html.append("<" + tag + ">");
	}

	@Override public void exitAtxHeading(@NotNull MarkdownParser.AtxHeadingContext ctx) {
		String tag = "h" + ctx.ATX_START().getText().length();
		html.append("</" + tag + ">");
	}

	@Override public void enterHorizontalRule(@NotNull MarkdownParser.HorizontalRuleContext ctx) {
		html.append("<hr />");
	}
	
	/**
	 * Missing internal parsing of blockquote
	 */
	@Override public void enterBlockQuote(@NotNull MarkdownParser.BlockQuoteContext ctx) {
		html.append("<blockquote>\n");
		Iterator<ParseTree> iter = ctx.children.listIterator();
		StringBuffer blockquoteText = new StringBuffer();
		while (iter.hasNext()) {
			ParseTree child = iter.next();
			if (child instanceof MarkdownParser.RawLineContext)
				blockquoteText.append(((MarkdownParser.RawLineContext)child).getText());
			else if (child instanceof MarkdownParser.BlockQuoteBlankLineContext)
				blockquoteText.append("\n");
		}
		html.append(translate(blockquoteText.toString()));
		html.append("\n</blockquote>");
	}

	@Override public void enterVerbatim(@NotNull MarkdownParser.VerbatimContext ctx) {
		html.append("<pre><code>");
		Iterator<ParseTree> iter = ctx.children.listIterator();
		while (iter.hasNext()) {
			ParseTree child = iter.next();
			if (child instanceof MarkdownParser.RawLineContext) {
				RawLineContext lineCtx = (RawLineContext)child;
				String originalText = lineCtx.getText();
				int count = (ctx._level+1)*4;
				while (count > 0) {
					if (originalText.charAt(0) == ' ')
						count--;
					else   // Tab.
						count = count-4;
					originalText = originalText.substring(1);
				}
				html.append(escapeHTML(originalText.replaceAll("\t", "    ")));
			} else if (child instanceof MarkdownParser.VerbatimBlankLineContext)
				html.append("\n");
		}
		html.append("</code></pre>");
	}
	
	@Override public void enterPara(@NotNull MarkdownParser.ParaContext ctx) {
		html.append("<p>");
	}

	@Override public void exitPara(@NotNull MarkdownParser.ParaContext ctx) {
		html.append("</p>");
		// TODO: this should be done every time a NEWLINE in encountered and not only leaving a paragraph.
		nextNewline = ctx.NEWLINE().getText();
	}
	
	@Override public void enterInline(@NotNull MarkdownParser.InlineContext ctx) {
		if (ctx.LINE_BREAK() != null) {
			html.append("<br />\n");
		} else if (ctx.SPACE() != null) {
			html.append(" ");
		} else if (ctx.TAB() != null) {
			html.append("    ");
		} else if (ctx.AMPERSAND() != null) {
			// DOC: normal content should not be always escaped, for instance ' and " should not.
			// for this reason here I check only ampersand.
			html.append("&amp;");
		} else if (ctx.SINGLE_QUOTE() != null) {
			html.append("'");
		} else if (ctx.DOUBLE_QUOTE() != null) {
			html.append("\"");
		} else if (ctx.span() == null) {
			html.append(escapeHTML(ctx.getText()));
		}
	}
	
	@Override public void enterEmph(@NotNull MarkdownParser.EmphContext ctx) {
		html.append("<em>");
	}

	@Override public void exitEmph(@NotNull MarkdownParser.EmphContext ctx) {
		html.append("</em>");
	}
	
	@Override public void enterStrong(@NotNull MarkdownParser.StrongContext ctx) {
		html.append("<strong>");
	}

	@Override public void exitStrong(@NotNull MarkdownParser.StrongContext ctx) {
		html.append("</strong>");
	}

	@Override public void enterLink(@NotNull MarkdownParser.LinkContext ctx) {
		String title = null;
		String url = "";
		if (ctx.explicitLink() != null) {
			if (ctx.explicitLink().linkTitle() != null)
				title = ctx.explicitLink().linkTitle().getText();
			if (ctx.explicitLink().linkUrl() != null)
				url = ctx.explicitLink().linkUrl().getText();
		} else if (ctx.referenceLink() != null) {
			String id = null;
			if (ctx.referenceLink().referenceId() != null) {
				id = ctx.referenceLink().referenceId().getText();
			} else {
				// Remove [ and ] . TODO: do it with the parser.
				id = removeFirstAndLast(ctx.linkContent().getText());
			}
			id = id.toLowerCase();
			MarkdownParser.ReferenceContext reference = references.get(id);
			if (reference == null) {
				// Not found reference and closing ] will be added by the exit method.
				html.append("[");
				return;
			}
			if (reference.referenceTitle() != null)
				title = reference.referenceTitle().getText();
			if (reference.referenceUrl() != null) {
				url = reference.referenceUrl().getText();
				// TODO: remove < and > using the parser.
				if (url.startsWith("<"))
					url = removeFirstAndLast(url);
			}
		} else {   // A shortcut like [simple case].
			String id = removeFirstAndLast(ctx.linkContent().getText());
			MarkdownParser.ReferenceContext reference = references.get(id);
			if (reference != null)
				url = reference.referenceUrl().getText();
			else {
				// Not found reference and closing ] will be added by the exit method.
				html.append("[");
				return;
			}
		}
		// TODO: remove < and > with the parser when present.
		if (url.startsWith("<") && url.endsWith(">"))
			url = removeFirstAndLast(url);
		url = escapeHTML(url);
		html.append("<a href=\"").append(url).append("\"");
		if (title != null) {
			title = removeFirstAndLast(title);   // Remove quoting.
			html.append(" title=\"")
			.append(escapeHTML(title))
			.append("\"");
		}
		html.append(">");
	}

	@Override public void exitLink(@NotNull MarkdownParser.LinkContext ctx) {
		if (ctx.referenceLink() != null) {
			// Code duplicated.
			String id = null;
			if (ctx.referenceLink().referenceId() != null) {
				id = ctx.referenceLink().referenceId().getText();
			} else {
				id = removeFirstAndLast(ctx.linkContent().getText());
			}
			id = id.toLowerCase();
			MarkdownParser.ReferenceContext reference = references.get(id);
			if (reference == null) {
				// Not found reference and closing ] will be added by the exit method.
				html.append("]");
				// This permits to do not lost existing spaces between content and reference link.
				html.append(ctx.referenceLink().getText());
				return;
			} else
				html.append("</a>");
		} else {
			if (ctx.explicitLink() != null) {
				html.append("</a>");
				return;
			}
			String id = removeFirstAndLast(ctx.linkContent().getText());
			MarkdownParser.ReferenceContext reference = references.get(id);
			if (reference != null)
				html.append("</a>");
			else {
				// Not found reference and closing ] will be added by the exit method.
				html.append("]");
			}
		}
	}
	
	@Override public void enterImageLink(@NotNull MarkdownParser.ImageLinkContext ctx) {
		String title = null;
		String alt = null;
		String url = null;
		if (ctx.imageAlt() != null)
			alt = ctx.imageAlt().getText();
		if (ctx.explicitImageLink() != null) {
			if (ctx.explicitImageLink().linkTitle() != null)
				title = ctx.explicitImageLink().linkTitle().getText();
			if (ctx.explicitImageLink().linkUrl() != null)
				url = ctx.explicitImageLink().linkUrl().getText();
			
		} else if (ctx.referenceImageLink() != null) {
			String id = ctx.referenceImageLink().referenceId().getText().toLowerCase();
			MarkdownParser.ReferenceContext reference = references.get(id);
			if (reference == null)
				System.err.println("Reference not found: " + id);
			if (reference.referenceTitle() != null)
				title = reference.referenceTitle().getText();
			if (reference.referenceUrl() != null)
				url = reference.referenceUrl().getText();
		}
		html.append("<img src=\"" + url + "\"");
		html.append(" alt=\"");
		if (alt != null)
			html.append(removeFirstAndLast(alt));
		html.append("\"");
		if (title != null) {
			html.append(" title=\"")
			.append(escapeHTML(removeFirstAndLast(title)))
			.append("\"");
		}
		html.append(" />");
	}

	@Override public void enterEntity(@NotNull MarkdownParser.EntityContext ctx) {
		html.append(ctx.getText());
	}

	@Override public void enterEscapedChar(@NotNull MarkdownParser.EscapedCharContext ctx) {
		String c = ctx.getText().substring(1);   // Remove "\".
		html.append(escapeHTML(c));
	}

	@Override public void enterCode(@NotNull MarkdownParser.CodeContext ctx) {
		String content = ctx.getText();
		int charNum = 0;
		if (ctx.backtickCode() != null)
			charNum = 1;
		else if (ctx.doubleBacktickCode() != null) {
			charNum = 2;
		} else if (ctx.spaceBacktickCode() != null) {
			charNum = 3;
		}
		content = content.substring(charNum, content.length()-charNum);
		html.append("<code>").append(escapeHTML(content)).append("</code>");
	}

	@Override public void enterBulletList(@NotNull MarkdownParser.BulletListContext ctx) {
		if (indentList && listLevel > 0) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		if (ctx._level > 0)
			html.append("\n");
		html.append("<ul>\n");
		listLevel++;
	}

	@Override public void exitBulletList(@NotNull MarkdownParser.BulletListContext ctx) {
		listLevel--;
		if (indentList && listLevel > 0) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		html.append("</ul>");
		if (indentList && listLevel > 0) {
			html.append("\n");
		}
	}

	@Override public void enterOrderedList(@NotNull MarkdownParser.OrderedListContext ctx) {
		if (indentList && listLevel > 0) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		html.append("<ol>\n");
		listLevel++;
	}

	@Override public void exitOrderedList(@NotNull MarkdownParser.OrderedListContext ctx) {
		listLevel--;
		if (indentList && listLevel > 0) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		html.append("</ol>");
	}

	@Override public void enterBulletListItem(@NotNull MarkdownParser.BulletListItemContext ctx) {
		if (indentList) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		html.append("<li>");
		MarkdownParser.BulletListContext bulletCtx = (MarkdownParser.BulletListContext)ctx.parent;
		//
		int index = bulletCtx.children.indexOf(ctx);
		boolean nextBeginBL = false;
		boolean nextInnerBL = false;
		if (index < bulletCtx.getChildCount()-1) {
			MarkdownParser.BulletListItemContext nextCtx = (MarkdownParser.BulletListItemContext)bulletCtx.getChild(index+1);
			nextInnerBL = nextCtx.listItemBlankLine().size() > 0;
			nextBeginBL = nextCtx.BLANK_LINE().size() > 0;
		}
		//
		boolean beginBL = ctx.BLANK_LINE().size() > 0;
		boolean innerBL = ctx.listItemBlankLine().size() > 0;
		// This item is followed by BLANK_LINE* and is not last item.
		boolean isSetPara = innerBL;
		ctx.isWithPar = beginBL || innerBL || nextBeginBL;
		bulletCtx.nextItemWithPar = isSetPara || nextInnerBL;
	}
	
	@Override public void enterOrderedListItem(@NotNull MarkdownParser.OrderedListItemContext ctx) {
		if (indentList) {
			html.append(INDENT_SPACES.substring(0, listLevel*listIndentSpaces));
		}
		html.append("<li>");
		MarkdownParser.OrderedListContext listCtx = (MarkdownParser.OrderedListContext)ctx.parent;
		//
		int index = listCtx.children.indexOf(ctx);
		boolean nextItemWithPar = false;
		if (index < listCtx.getChildCount()-1) {
			MarkdownParser.OrderedListItemContext nextCtx = (MarkdownParser.OrderedListItemContext)listCtx.getChild(index+1);
			nextItemWithPar = nextCtx.listItemBlankLine().size() > 0
					|| nextCtx.BLANK_LINE().size() > 0;
		}
		// This item is followed by BLANK_LINE* and is not last item.
		boolean isSetPara = false;
		if (ctx.listItemBlankLine().size() > 0
				|| (ctx.BLANK_LINE().size() > 0 && !ctx.parent.getChild(ctx.parent.getChildCount()-1).equals(ctx))) {
			ctx.isWithPar = true;
			isSetPara = true;   // Pass property to next item.
		} else if ((listCtx != null && listCtx.nextItemWithPar) || nextItemWithPar) {
			ctx.isWithPar = true;
		}
		if (listCtx != null)
			listCtx.nextItemWithPar = isSetPara;
	}
	
	@Override
	public void enterInlineListItem(InlineListItemContext ctx) {
		if (ctx.parent instanceof MarkdownParser.BulletListItemContext
				&& ((MarkdownParser.BulletListItemContext)ctx.parent).isWithPar) {
			html.append("<p>");
		} else if (ctx.parent instanceof MarkdownParser.OrderedListItemContext
				&& ((MarkdownParser.OrderedListItemContext)ctx.parent).isWithPar) {
			html.append("<p>");
		}
	};
	
	@Override
	public void exitInlineListItem(InlineListItemContext ctx) {
		// Remote last \n. TODO: see how to do it from the parser.
		if (html.charAt(html.length()-1) == '\n')
			html.deleteCharAt(html.length()-1);
		if (ctx.parent instanceof MarkdownParser.BulletListItemContext
				&& ((MarkdownParser.BulletListItemContext)ctx.parent).isWithPar) {
			html.append("</p>");
		} else if (ctx.parent instanceof MarkdownParser.OrderedListItemContext
				&& ((MarkdownParser.OrderedListItemContext)ctx.parent).isWithPar) {
			html.append("</p>");
		}
	};
	
	@Override
	public void enterListItemBlankLine(ListItemBlankLineContext ctx) {
		html.append("\n\n");
	};
	
	@Override public void exitBulletListItem(@NotNull MarkdownParser.BulletListItemContext ctx) {
		html.append("</li>\n");
	}
	
	@Override public void exitOrderedListItem(@NotNull MarkdownParser.OrderedListItemContext ctx) {
		html.append("</li>\n");
	}
	
	private static Random random = new Random(0x1234);
	private static String encodeEmailUrl(String url) {
		if (url == null)
			return null; 
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			// 10% raw, 45% hex, 45% decimal, see Markdown.pl as reference.
			int r = random.nextInt(100);
			if (r > 90)
				result.append(escapeHTML(String.valueOf(c)));
			else if (r < 45)
				result.append("&#x").append(Integer.toHexString(c)).append(";");
			else
				result.append("&#").append((int) c).append(";");
		}
		//
		return result.toString();
	}

	@Override public void enterAutolink(@NotNull MarkdownParser.AutolinkContext ctx) {
		String url = removeFirstAndLast(ctx.getText());
		if (url.contains("@")) {
			url = encodeEmailUrl(url);
		} else {
			url = escapeHTML(url);
		}
		html.append("<a href=\"").append(url).append("\"");
		html.append(">").append(url).append("</a>");
	}
	
	@Override
	public void exitHtmlBlockTags(HtmlBlockTagsContext ctx) {
		// Output only trailing part after html close.
		String content = ctx.getText();
		content = content.substring(content.lastIndexOf(">")+1);
		// Remove last \n.
		content = content.substring(0, content.length()-1);
		html.append(content);
	};

	@Override
	public void enterHtmlBlockInTags(HtmlBlockInTagsContext ctx) {
		String block = ctx.getText();
		// Remove \n at the end that we should match to match LINE_BREAK.
		if (block.endsWith("\n"))
			block = block.substring(0, block.length()-1);
		html.append(block.replaceAll("\t", "    "));
	}

	@Override
	public void enterHtmlComment(HtmlCommentContext ctx) {
		String comment = ctx.getText();
		// Remove \n at the end that we should match to match LINE_BREAK.
		if (comment.endsWith("\n"))
			comment = comment.substring(0, comment.length()-1);
		comment = comment.replaceAll("\t", "    ");
		html.append(comment);
	}
	
}
