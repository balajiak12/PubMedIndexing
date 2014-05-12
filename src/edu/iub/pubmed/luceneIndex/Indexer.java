package edu.iub.pubmed.luceneIndex;

/**
 * @author Balaji
 */

import java.io.IOException;
import java.io.File;
import java.util.List;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import edu.iub.pubmed.model.*;
import edu.iub.pubmed.parsing.ArticleParser;
import edu.iub.pubmed.properties.Properties;
 
public class Indexer implements Properties {

	private ArticleParser articleParser;
	private IndexWriter indexWriter = null;

	/** Creates a new instance of Indexer */
	public Indexer(List<Article> listArticle) {
		articleParser = new ArticleParser(listArticle);
	}

	public IndexWriter getIndexWriter(boolean create) throws IOException {
		Directory dir = FSDirectory.open(new File(luceneIndexPath));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45,
				analyzer);
		if (indexWriter == null || create) {
			indexWriter = new IndexWriter(dir, iwc);
		}
		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public void indexArticle(Article article) throws IOException {

		System.out.println("Indexing Article: " + article);
		// Get the IndexWriter to index the details for this Article
		IndexWriter writer = null;
		try {
			writer = getIndexWriter(false);
		} catch (IOException e1) {
			System.out.println("Exception Occured:" + e1.getStackTrace());
		}
		Document doc = new Document();

		doc.add(new StringField("id", article.getPubmedId(), Field.Store.YES));

		if (!article.getArticleTitle().isEmpty()) {
			doc.add(new TextField("Title", article.getArticleTitle()
					.toLowerCase(), Field.Store.YES));
			String[] articleTitle = article.getArticleTitle().split(" ");
			for (int idx = 0; idx < articleTitle.length; idx++) {
				doc.add(new StringField("ArticleTitle", articleTitle[idx]
						.toLowerCase(), Field.Store.YES));
			}
		}

		if (!article.getAuthors().isEmpty()) {
			List<Author> authors = article.getAuthors();
			for (Author author : authors) {
				doc.add(new StringField("GivenName", author.getGivenName()
						.toLowerCase(), Field.Store.YES));
				doc.add(new StringField("SurName", author.getSurname()
						.toLowerCase(), Field.Store.YES));
			}
		}

		if (!article.getKeywords().isEmpty()) {
			List<Keyword> keywords = article.getKeywords();
			for (Keyword keyword : keywords) {
				doc.add(new StringField("KwdGroupType", keyword
						.getKwdGroupType().toLowerCase(), Field.Store.YES));
				doc.add(new StringField("KeywordText", keyword.getKeywordText()
						.toLowerCase(), Field.Store.YES));
			}
		}
		
		if (!article.getCategories().isEmpty()) {
			List<Category> categories = article.getCategories();
			for (Category category : categories) {
				doc.add(new StringField("Subject", category
						.getSubject().toLowerCase(), Field.Store.YES));
			}
		}


		if (!article.getAbstractText().isEmpty()) {
			doc.add(new TextField("Abstract", article.getAbstractText().toLowerCase(),
					Field.Store.YES));
			String[] articleAbstract = article.getAbstractText().split(" ");
			for (int idx = 0; idx < articleAbstract.length; idx++) {
				doc.add(new StringField("ArticleAbstract", articleAbstract[idx]
						.toLowerCase(), Field.Store.YES));
			}
		}
		// add the document to the writer
		try {
			writer.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void rebuildIndexes() throws IOException {
		System.out.println("before getIndexWriter(true)");
		getIndexWriter(true);
		System.out.println("after getIndexWriter(true)");
		List<Article> list = articleParser.getList();
		System.out.println("Arraylist: " + list);
		for (Article article : list) {
			indexArticle(article);
		}
		System.out.println("after indexing article");
		closeIndexWriter();
	}
}