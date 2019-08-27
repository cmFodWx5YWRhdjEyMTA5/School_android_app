package com.microbotic.temperature.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.cyberlink.faceme.FaceAttribute;
import com.cyberlink.faceme.FaceFeature;
import com.microbotic.temperature.utils.BitmapUtility;

import java.io.File;

public class StudentTest {

    private int id;
    private String name,className;
    private Bitmap photo;
    private String imageName;
    private FaceFeature faceFeature;
    private FaceAttribute FaceAttribute;

    public StudentTest(int id, String name, String className, byte[] photo) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.photo = BitmapUtility.getImage(photo);
    }

    public StudentTest(int id, String name, String className, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.photo = photo;
    }

    public StudentTest(int id, String name, String className, String imageName) {
        this.id = id;
        this.name = name;
        this.className = className;
        this.imageName = imageName;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        this.faceFeature = faceFeature;
    }

    public com.cyberlink.faceme.FaceAttribute getFaceAttribute() {
        return FaceAttribute;
    }

    public void setFaceAttribute(com.cyberlink.faceme.FaceAttribute faceAttribute) {
        FaceAttribute = faceAttribute;
    }

    public String getImageName() {
        return imageName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getPhotoArray() {
        return BitmapUtility.getBytes(photo);
    }

    public Bitmap getPhoto() {

        return photo;
    }
}
