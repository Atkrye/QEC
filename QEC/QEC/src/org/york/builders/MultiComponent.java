package org.york.builders;

import org.york.yqcgp.QuantumSystem;

public interface MultiComponent {
	
	public void addComponent(String name, Object[] args);
	public QuantumSystem buildComponent(String name);
	public QuantumSystem buildComponentBest(String name);
}
