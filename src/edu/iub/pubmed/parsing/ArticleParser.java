package edu.iub.pubmed.parsing;

/**
 * @author Balaji 
 */
import java.io.IOException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import edu.iub.pubmed.model.*;

public class ArticleParser {

	XPath path = null;
	Article article = null;
	String fileName = null;
	List<Article> listArticle;

	public ArticleParser(List<Article> articleList) {
		this.listArticle = articleList;
	}

	public ArticleParser(Article article, List<Article> articleList,
			String fileName) {
		path = XPathFactory.newInstance().newXPath();
		this.article = article;
		this.fileName = fileName;
		this.listArticle = articleList;

	}
	
	/**
	 * To create document factory 
	 */

	public Document parseFile() throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(false);
		dbFactory.setNamespaceAware(true);
		dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
		dbFactory.setFeature("http://xml.org/sax/features/validation", false);
		dbFactory
				.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
						false);
		dbFactory
				.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fileName);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	/**
	 * Parse the XML file and extracts all the required values 
	 * @throws Exception 
	 */
	public void parse(String fileName) throws Exception {
		Document document = parseFile();
		extractArticleMetaData(document);
		extractAuthorInformation(document);
		// extractConferenceDetails(document);
		extractKeyWords(document);
		extractArticleCategories(document);

	}

	
	public void extractArticleMetaData(Document document) throws IOException {
		String pubmedId = null;
		String articleTitle = null;
		String abstractText = null;

		try {
			Node pubMedNode = (Node) path
					.evaluate(
							"/article/front/article-meta/article-id[@pub-id-type='pmid']",
							document.getDocumentElement(), XPathConstants.NODE);
			if (pubMedNode == null || pubMedNode.getTextContent() == null) {
				throw new Exception("No PubMed Id");
			}
			pubmedId = pubMedNode.getTextContent();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		try {
			articleTitle = (String) path
					.evaluate(
							"/article/front/article-meta/title-group/article-title/text()",
							document.getDocumentElement(),
							XPathConstants.STRING);
			if (articleTitle == null) {
				throw new Exception("No Article Title");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		try {
			Node abstractNode = (Node) path.evaluate(
					"/article/front/article-meta/abstract",
					document.getDocumentElement(), XPathConstants.NODE);
			if (abstractNode == null || abstractNode.getTextContent() == null) {
				throw new Exception("No Abstract Text");
			}
			abstractText = abstractNode.getTextContent();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		if (pubmedId != null || articleTitle != null || abstractText != null) {
			article.setPubmedId(pubmedId);
			System.out.println("pubmedId: " + pubmedId);
			article.setArticleTitle(articleTitle);
			System.out.println("articleTitle: " + articleTitle);
			article.setAbstractText(abstractText);
			System.out.println("abstractText: " + abstractText);
			listArticle.add(article);
			System.out.println("List: " + listArticle);
		}
	}

	/**
	 * Extracts the Conference node from article-meta and traverses to its child
	 * Nodes to get conference details.Conference element is present at following locations 
	 * <article><article-meta><conference> . This conference element have all the required 
	 * child nodes to populate conference table . <br>
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	private void extractConferenceDetails(Document document) throws Exception {
		Conference conf = null;
		NodeList conferenceNode = null;
		try {
			conferenceNode = (NodeList) path.evaluate(
					"/article/front/article-meta/conference",
					document.getDocumentElement(), XPathConstants.NODESET);
			System.out.println("Conference Node: " + conferenceNode);
			if (conferenceNode == null || conferenceNode.getLength() == 0) {
				System.out.println("No conferences found for this article:"
						+ fileName);
				// throw new Exception("No conferences");
			}

			if (conferenceNode != null) {
				Element conferenceElement = (Element) conferenceNode.item(0);
				conf = new Conference();
				for (int index = 0; index < conferenceElement.getChildNodes()
						.getLength(); index++) {
					Element childNode = (Element) conferenceElement
							.getChildNodes().item(index);
					String nodeName = childNode.getNodeName();
					if (nodeName.equals("conf-date")) {
						// TO Do traverse further to find the Day,Month , Year
						Date date = new Date(System.currentTimeMillis());
						conf.setDate(date);
					} else if (nodeName.equals("conf-name")) {
						conf.setName(childNode.getTextContent());
					} else if (nodeName.equals("conf-loc")) {
						conf.setLoc(childNode.getTextContent());
					} else if (nodeName.equals("conf-sponsor")) {
						conf.setSponsor(childNode.getTextContent());
					} else if (nodeName.equals("conf-acronym")) {
						conf.setAcronym(childNode.getTextContent());
					} else if (nodeName.equals("conf-theme")) {
						conf.setTheme(childNode.getTextContent());
					} else if (nodeName.equals("conf-num")) {
						conf.setNum(childNode.getTextContent());
					}
				}
				article.setConference(conf);
				System.out.println(conf.getDate());
			}
		} catch (Exception ex) {
			System.out.println("Exception while parsing Conference details "
					+ fileName);
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * Extracts Author Information by retrieving the author nodes and traversing
	 * them to find the name of the author . Inserts into author and
	 * authorReference Table
	 * 
	 * @param document
	 */
	private void extractAuthorInformation(Document document) {
		List<Author> authors = null;
		Author author = null;
		try {
			NodeList authorsNodes = (NodeList) path
					.evaluate(
							"/article/front/article-meta/contrib-group/contrib[@contrib-type = 'author']",
							document.getDocumentElement(),
							XPathConstants.NODESET);
			if (authorsNodes == null || authorsNodes.getLength() == 0) {
				System.out.println("No authors found for this article"
						+ fileName);
				throw new Exception("No authors");
			}
			authors = new LinkedList<Author>();
			Element authorElement = null;
			Element nameElement = null;
			for (int index = 0; index < authorsNodes.getLength(); index++) {
				authorElement = (Element) authorsNodes.item(index);
				nameElement = (Element) authorElement.getElementsByTagName(
						"name").item(0);
				if (nameElement != null && nameElement.getChildNodes() != null) {
					author = new Author();
					for (int childIndex = 0; childIndex < nameElement
							.getChildNodes().getLength(); childIndex++) {
						Node nameChildNode = nameElement.getChildNodes().item(
								childIndex);
						if (nameChildNode.getNodeName() == "surname") {
							author.setSurname(nameChildNode.getTextContent());
						} else if (nameChildNode.getNodeName() == "given-names") {
							author.setGivenName(nameChildNode.getTextContent());
						}
					}
					authors.add(author);
				}
			}
			article.setAuthors(authors);
			System.out.println("Authors: " + authors.size());
		} catch (Exception ex) {
			System.out.println("Exception while parsing Author Information "
					+ fileName);
			ex.printStackTrace();
			return;
		}
	}

	/**
	 * <ul>
	 * <li>Extracts Keyword information from the XML file .</li>
	 * 
	 * <li>Author information is available at
	 * <article><article-meta><kwd-group><kwd></li>
	 * 
	 * </ul>
	 * 
	 */private void extractKeyWords(Document document) throws Exception {
		List<Keyword> keyWords = new LinkedList<Keyword>();
		Keyword keyword = null;
		NodeList keywordGroups = null;
		try {
			keywordGroups = (NodeList) path.evaluate(
					"/article/front/article-meta/kwd-group",
					document.getDocumentElement(), XPathConstants.NODESET);
			if (keywordGroups != null) {
				Element keywordGroup = null;
				String kwdGroupType = null;
				for (int index = 0; index < keywordGroups.getLength(); index++) {
					keywordGroup = (Element) keywordGroups.item(index);
					kwdGroupType = keywordGroup.getAttribute("kwd-group-type");
					for (int childIndex = 0; childIndex < keywordGroup
							.getChildNodes().getLength(); childIndex++) {
						Element keywordNode = (Element) keywordGroup
								.getChildNodes().item(childIndex);
						if (keywordNode.getNodeName() == "kwd") {
							keyword = new Keyword();
							keyword.setKwdGroupType(kwdGroupType);
							keyword.setKeywordText(keywordNode.getTextContent());
							keyWords.add(keyword);

						}
					}
				}
				article.setKeywords(keyWords);
			}
			System.out.println("Keywords: " + keyWords.size());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * Extracts category information Recursively i.e for each category if there
	 * are child categories they are traversed further.
	 * 
	 * @param document
	 * @throws Exception
	 */
	private void extractArticleCategories(Document document) throws Exception {
		List<Category> categories = new LinkedList<Category>();
		Category category = null;
		NodeList categoryNodes = null;
		try {
			categoryNodes = (NodeList) path
					.evaluate(
							"/article/front/article-meta/article-categories/subj-group",
							document.getDocumentElement(),
							XPathConstants.NODESET);
			if (categoryNodes != null) {
				Element categoryNode = null;
				for (int index = 0; index < categoryNodes.getLength(); index++) {
					categoryNode = (Element) categoryNodes.item(index);
					category = new Category();
					category.setSubject(categoryNode.getFirstChild()
							.getTextContent());
					categories.add(category);
					if (categoryNode.getChildNodes().getLength() > 1) {
						setSubCategories(categoryNode.getLastChild(), category,
								categories);
					}
				}
				article.setCategories(categories);
				System.out.println("Categories: " + categories.size());
			}
		} catch (Exception ex) {
			System.out
					.println("Exception while parsing Categories " + fileName);
			ex.printStackTrace();
			throw ex;
		}
	}

	private void setSubCategories(Node lastChild, Category category,
			List<Category> categories) {
		Category subCategory = null;
		subCategory = new Category();
		subCategory.setSubject(lastChild.getFirstChild().getNodeValue());
		categories.add(subCategory);
		if (lastChild.getChildNodes().getLength() > 1) {
			setSubCategories(lastChild.getLastChild(), subCategory, categories);
		}
	}

	public List<Article> getList() {
		System.out.println("article list: " + listArticle);
		return listArticle;
	}

}
