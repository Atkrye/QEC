package org.york.ants;

public class AntNodeHolder {

	public int path;
	public AntNode node;
	
	public AntNodeHolder(int path, AntNode node){
		this.path = path;
		this.node = node;
	}
	
	@Override
	public String toString(){
		return "Path: " + path + " " + node;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(o.getClass().equals(AntNode.class)){
			if(node.equals((AntNode)o)){
				return true;
			}
		}
		if(o.getClass().equals(AntNodeHolder.class)){
			AntNodeHolder an = (AntNodeHolder)o;
			if(an.node.equals(node) && path == an.path){
				return true;
			}
		}
		return false;
	}
}
