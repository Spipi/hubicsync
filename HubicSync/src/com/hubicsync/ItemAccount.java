package com.hubicsync;

import hubic.Access;

public class ItemAccount implements Comparable<ItemAccount>{

        private String name;
        private Access acc;
       
        public ItemAccount(String n, Access acc)
        {
                name = n;
                this.acc= acc;
                        
        }
        public String getName()
        {
                return name;
        }
        public Access getAcc()
        {
                return acc;
        }


        public int compareTo(ItemAccount o) {
                if(this.name != null)
                        return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
                else
                        throw new IllegalArgumentException();
        }

}
