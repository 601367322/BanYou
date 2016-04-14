package co.lujun.androidtagview;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Shen on 2016/2/26.
 */
public class TagBean implements Serializable {

    public int id;
    public String color;
    @SerializedName("dyType")
    public String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTextColor() {
        if (textColor.startsWith("#")) {
            return textColor;
        } else {
            return "#" + textColor;
        }
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getColor() {
        if (color.startsWith("#")) {
            return color;
        } else {
            return "#" + color;
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String textColor = "#FFFFFF";

    public TagBean() {
    }

    public TagBean(String color, String text, String textColor) {
        this.color = color;
        this.text = text;
        this.textColor = textColor;
    }
}
