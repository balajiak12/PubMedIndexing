package edu.iub.pubmed.searchIndex;
/*
 * SearchEngine.java
 *
 * Created on 6 March 2006, 14:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import edu.iub.pubmed.properties.Properties;

/**
 * 
 * @author Balaji
 */
public class SearchEngine implements Properties {
	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private IndexReader reader = null;
	private Analyzer analyzer = null;

	/** Creates a new instance of SearchEngine */
	public SearchEngine() throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(luceneIndexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_45);
		

	}

	public void performSearch(String queryString, String field) throws IOException,
			ParseException {
		parser = new QueryParser(Version.LUCENE_45, field, analyzer);
		Query query = parser.parse(queryString);
		TopDocs topDocs = searcher.search(query, 50);
		ScoreDoc[] hits = topDocs.scoreDocs;
		System.out.println("Found " + hits.length + " hits in XML element " + field);

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			System.out.println("Search string "+queryString+" is present in XML element "+ field+" in paper: " + doc.get("id"));
		}

	}
}
