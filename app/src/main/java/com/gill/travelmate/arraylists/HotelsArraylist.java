package com.gill.travelmate.arraylists;

//Pojo class for Hotels data
public class HotelsArraylist {

    String id,name,rating,address,image,reviews,distance,category,phone,url;
    Double latitude,longitude;

    public HotelsArraylist(String id,String name,String rating,String address,String image,Double latitude,Double longitude,String reviews,String distance,String category,String phone,String url){
        this.id=id;
        this.name=name;
        this.rating=rating;
        this.address=address;
        this.image=image;
        this.latitude=latitude;
        this.longitude=longitude;
        this.reviews=reviews;
        this.distance=distance;
        this.category=category;
        this.phone=phone;
        this.url=url;
    }

    public String getPhone() {
        return phone;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getReviews() {
        return reviews;
    }

    public String getDistance() {
        return distance;
    }
}
