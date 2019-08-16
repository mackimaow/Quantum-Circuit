package appFX.framework.gateModels;

import java.io.Serializable;
import java.util.HashSet;

import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;
import language.compiler.LexicalAnalyzer.LexemeNotRecognizedException;
import language.compiler.LexicalAnalyzer.LexicalAnaylizerIOException;
import mathLib.equation.BooleanEquation;
import mathLib.equation.BooleanEquation.InputBitIndexListener;
import mathLib.equation.BooleanEquationParser.BooleanEquationParseException;
import utils.customCollections.Single;
import utils.customCollections.immutableLists.ImmutableArray;

public class ClassicalGateDefinition extends GateDefinition implements Serializable {
	private static final long serialVersionUID = 1082500444022823298L;
	
	private ImmutableArray<BooleanEquation> definitions;
	
	public ClassicalGateDefinition(String ... userStrings) {
		this.userInput = new ImmutableArray<>(userStrings);
	}
	
	@Override
	protected void checkAndInitializeDefinition(String[] parameters) throws DefinitionEvaluatorException {
		BooleanEquation[] definitions = new BooleanEquation[userInput.size()];
		Single<Integer> largestInput = new Single<>(0);
		
		if(userInput.size() <= 0)
			throw new DefinitionEvaluatorException("This gate has no definitions. There must be at least one definition", -1);
		
		InputBitIndexListener listener = (i) -> {
			if(i+1 > largestInput.first())
				largestInput.setFirst(i+1);
		};

		HashSet<Integer> outputs = new HashSet<>();
		String[] latexStrings = new String[userInput.size()]; 
		for(int i = 0; i < userInput.size(); i++) {
			try {
				definitions[i] = new BooleanEquation(userInput.get(i), listener);
				latexStrings[i] = definitions[i].getLatexString();
				int outputBitIndex = definitions[i].getOutputBitIndex();
				if(outputBitIndex + 1 > largestInput.first())
					largestInput.setFirst(outputBitIndex + 1);
				if(!outputs.add(outputBitIndex))
					throw new BooleanEquationParseException("Multiple definitions for the output bit \"" + outputBitIndex +  "\"");
			} catch(LexemeNotRecognizedException | LexicalAnaylizerIOException| BooleanEquationParseException exception) {
				throw new DefinitionEvaluatorException(exception.getMessage(), i);
			}
		}
		
		this.definitions = new ImmutableArray<BooleanEquation>(definitions);
		this.latex = new ImmutableArray<>(latexStrings);
		this.numberOfRegisters = largestInput.first();
	}
	
	public ImmutableArray<BooleanEquation> getDefinitions() {
		return definitions;
	}
}
