/* Copyright 2009-2012 David Hadka
 * 
 * This file is part of the MOEA Framework.
 * 
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * The MOEA Framework is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.operator.program;

import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

public class PointMutation implements Variation {
	
	private double probability;
	
	public PointMutation(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();
		
		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);
			
			if (variable instanceof Program) {
				Program program = (Program)variable;
				mutate(program, program.getRules());
			}
		}
		
		return new Solution[] { result };
	}
	
	protected void mutate(Node node, Rules rules) {
		for (int i = 0; i < node.getNumberOfArguments(); i++) {
			if (PRNG.nextDouble() <= probability) {
				Node argument = node.getArgument(i);
				List<Node> mutations = rules.listAvailableMutations(argument);
				
				if (!mutations.isEmpty()) {
					//apply the mutation
					Node mutation = PRNG.nextItem(mutations);
					node.setArgument(i, mutation);
					
					for (int j = 0; j < argument.getNumberOfArguments(); j++) {
						mutation.setArgument(j, argument.getArgument(j));
					}
					
					argument = mutation;
				}
				
				for (int j = 0; j < argument.getNumberOfArguments(); j++) {
					mutate(argument.getArgument(j), rules);
				}
			}
		}
	}

}