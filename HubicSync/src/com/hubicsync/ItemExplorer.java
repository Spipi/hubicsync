package com.hubicsync;

public class ItemExplorer implements Comparable<ItemExplorer>{

        private String name;
        private String path;
        private String img;
        private Boolean sync;
        public ItemExplorer(String p, String n, String img, Boolean sync)
        {	
        		path = p;
                name = n;
                this.img = img;
                this.sync = sync;
                        
        }
        public String getName()
        {
                return name;
        }
        public String getPath()
        {
                return path;
        }
        public Boolean getSync()
        {
                return sync;
        }


        public int compareTo(ItemExplorer o) {
        	if(this.name != null && o.getName()!=null){
        		String nameThis = this.name.toLowerCase();
        		String nameOther = o.getName().toLowerCase();
	        	if(nameOther.endsWith("/")&&!nameThis.endsWith("/") || nameOther.equals(".."))
	        		return 1;
	        	else if(!nameOther.endsWith("/")&&nameThis.endsWith("/") || name.equals(".."))
	        		return -1;
	        	else
	        		return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        	}
                else
                        throw new IllegalArgumentException();
        }
		public void getPathItem() {
			// TODO Auto-generated method stub
			
		}
		public String getImage() {
			// TODO Auto-generated method stub
			return this.img;
		}

}
