package com.example.afs.ihome;

/**
 * Created by fguimaraes on 09/07/2017.
 */

public class Mydevices {<
    private int icon;
    private String title;
    private String address;
    private String type;

    public Mydevices(int icon,String Title, String address, String Type)
    {
        this.setIcon(icon);
        this.setTitle(Title);
        this.setAddress(address);
        this.setType(Type);
    }

    public void setIcon(int Icon){
        this.icon=Icon;
    }

    public void setTitle(String Title){
        this.title=Title;
    }

    public void setAddress(String Address){
        this.address=Address;
    }

    public void setType(String Type){
        this.type=Type;
    }


    public int getIcon(){
        return  this.getIcon();
    }

    public String getTitle(){
        return new String(this.getTitle());
    }

    public String getAddress(){
        return new String (this.getAddress());
    }

    public String getType()
    {
        return new String(this.getType());
    }

    public String toString(){
        return new String(".....");
    }
}
