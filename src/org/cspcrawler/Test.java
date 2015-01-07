package org.cspcrawler;

import java.util.List;

import org.eclipse.wst.jsdt.core.dom.*;
import org.eclipse.core.runtime.*;

public class Test {

	public static void main(String[] args) {
		String s = "var x = 5; var y = 6; var z = x + y; document.getElementById(\"demo\").innerHTML = z;";
		char[] in = s.toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(in);
		ASTNode root = parser.createAST(null);
		/*
		JavaScriptUnit result = (JavaScriptUnit) parser.createAST(null);
		List<Object> l = result.statements();
		System.out.println(l.size());
		for(Object o : l){
			System.out.println(o.toString());
		}
		*/
		System.out.println(root.toString());
		System.out.println(root.getLength());



	}

}
