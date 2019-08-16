package testLib;

import java.util.List;

import javax.swing.JPanel;

import language.compiler.LexicalAnalyzer.LexemeNotRecognizedException;
import language.compiler.LexicalAnalyzer.LexicalAnaylizerIOException;
import mathLib.equation.BooleanEquation;
import mathLib.equation.BooleanEquationParser.BooleanEquationParseException;

@SuppressWarnings("serial")
public class Test extends JPanel {
	
	public static void main(String[] args) throws LexemeNotRecognizedException, LexicalAnaylizerIOException, BooleanEquationParseException {
		BooleanEquation equ = new BooleanEquation("b[0] = b[0] b[1]");
	}

}
