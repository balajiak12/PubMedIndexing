package edu.iub.pubmed.model;

import java.util.List;

import edu.iub.pubmed.parsing.ArticleParser;

public class PubmedData {

	public void loadFileToDB(List<Article> articleList,String fileName){
		try{
			Article article = new Article();
			ArticleParser parser = new ArticleParser(article,articleList,fileName);
			parser.parse(fileName);
			System.out.println("Loading Completed");
			return;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
