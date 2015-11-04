package com.zhm.duxiangle.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhm(183340093@qq.com)
 *         2015��10��7��
 */
public class Book implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;//
    private String title;
    private String subtitle;
    private List<String> author;//作者
    private List<String> translator;//翻译 ������
    private String price;
    private String publisher;//
    private String catalog;//
    private String summary;//
    private String author_intro;
    private String isbn10;
    private String isbn13;
    private String url;//
    private String alt;//

    private Rating rating;//
    private Images images;//
    private Series series;//
    private String pages;//
    private String image;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    //作者信息
    private String strAuthor;
    private String strTranslator;

    public String getStrTranslator() {
        return strTranslator;
    }

    public void setStrTranslator(String strTranslator) {
        this.strTranslator = strTranslator;
    }

    public String getStrAuthor() {
        return strAuthor;
    }

    public void setStrAuthor(String strAuthor) {
        this.strAuthor = strAuthor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTranslator() {
        return translator;
    }

    public void setTranslator(List<String> translator) {
        this.translator = translator;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getAuthor_intro() {
        return author_intro;
    }

    public void setAuthor_intro(String author_intro) {
        this.author_intro = author_intro;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + ", images=" + images + title + ", subtitle=" + subtitle + ", author=" + author + ", translator="
                + translator + ", price=" + price + ", publisher=" + publisher + ", catalog=" + catalog + ", summary="
                + summary + ", author_intro=" + author_intro + ", isbn10=" + isbn10 + ", isbn13=" + isbn13 + ", url="
                + url + ", alt=" + alt + ", rating=" + rating + ", series=" + series + ", pages="
                + pages + "]";
    }


}
