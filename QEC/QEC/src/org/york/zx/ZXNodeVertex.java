package org.york.zx;

public class ZXNodeVertex {
		private int x;
		private int y;
		public ZXNodeVertex(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString(){
			return x + ":" + y;
		}
		
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		
		public ZXNodeVertex copy(){
			return new ZXNodeVertex(x, y);
		}
		
		@Override
		public boolean equals(Object obj){
			if(obj.getClass().equals(this.getClass())){
				ZXNodeVertex o = (ZXNodeVertex)obj;
				if(o.getX() == this.getX() && o.getY() == this.getY()){
					return true;
				}
			}
			return false;
		}
		
		public boolean equals(int x, int y){
			if(this.getX() == x && this.getY() == y){
				return true;
			}
			return false;
		}
}
