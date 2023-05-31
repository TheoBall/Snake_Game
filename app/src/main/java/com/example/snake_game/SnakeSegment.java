package com.example.snake_game;

import android.content.Context;
import android.widget.ImageView;

public class SnakeSegment {
    private float previousX;
    private float previousY;
    private ImageView segmentImage;

    public SnakeSegment(ImageView image) {
        this.segmentImage = image;
    }

    public float getPreviousX() {
        return previousX;
    }

    public float getPreviousY() {
        return previousY;
    }

    public void setX(float newX) {
        segmentImage.setX(newX);
    }

    public void setY(float newY) {
        segmentImage.setY(newY);
    }

    public float getX() {
        return segmentImage.getX();
    }

    public float getY() {
        return segmentImage.getY();
    }

    public float getWidth() {
        return segmentImage.getWidth();
    }

    public float getHeight() {
        return segmentImage.getHeight();
    }

    /**
     * Bouge le segment par rapport au segment qui le précède
     * @param posX la prochaine position X à avoir
     * @param posY la prochaine position Y à avoir
     */
    public void moveSegment(float posX, float posY) {
        previousX = segmentImage.getX();
        previousY = segmentImage.getY();
        segmentImage.setX(posX);
        segmentImage.setY(posY);
    }


}