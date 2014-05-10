package edu.iub.pubmed.main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import edu.iub.pubmed.luceneIndex.Indexer;
import edu.iub.pubmed.model.Article;
import edu.iub.pubmed.model.PubmedData;
import edu.iub.pubmed.searchIndex.SearchEngine;

public class Main {

	private PubmedData pubmedData;
	static List<Article> articleList;

	public Main() {
		pubmedData = new PubmedData();
		articleList = new LinkedList<Article>();
	}

	public void retrieveFiles(String path) {
		File rootDirectory = null;
		File[] subFiles = null;
		File currentFile = null;
		rootDirectory = new File(path);
		if (rootDirectory != null && rootDirectory.exists()) {
			subFiles = rootDirectory.listFiles();
			for (int index = 0; index < subFiles.length; index++) {
				currentFile = subFiles[index];
				if (currentFile.isFile()) {
					System.out.println("Current file: "
							+ currentFile.getAbsolutePath());
					pubmedData.loadFileToDB(articleList,
							currentFile.getAbsolutePath());
				} else if (currentFile.isDirectory()) {
					retrieveFiles(currentFile.getAbsolutePath());
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			Main mainMethod = new Main();
			mainMethod
					.retrieveFiles("C:\\Users\\Balaji\\Desktop\\pub-med-mining\\DataSet\\Archive1\\Int_J_Ment_Health_Syst");

			System.out.println("rebuildIndexes");
			Indexer indexer = new Indexer(articleList);
			indexer.rebuildIndexes();
			System.out.println("rebuildIndexes done");

			System.out.println("performSearch");
			SearchEngine searchEngine = new SearchEngine();
			System.out.println("Enter the Keyword to be searched: ");

			String[] searchStrings = { "ArticleTitle", "GivenName", "SurName",
					"KwdGroupType", "KeywordText", "ArticleAbstract", "Subject" };
			for (String searchField : searchStrings) {
				searchEngine.performSearch("mental", searchField);
			}
			System.out.println("performSearch done");

		} catch (Exception e) {
			System.out.println("Exception caught.\n");
			e.printStackTrace();
		}
	}
}
