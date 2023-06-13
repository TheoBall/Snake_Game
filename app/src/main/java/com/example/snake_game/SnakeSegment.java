package com.example.snake_game;

import android.widget.ImageView;

public class SnakeSegment {
    private float previousX;
    private float previousY;
    private ImageView segmentImage;

    /**
     * Constructeur de Segment
     * @param image image du segment
     */
    public SnakeSegment(ImageView image) {
        this.segmentImage = image;
    }

    /**
     * @return la précédante position X
     */
    public float getPreviousX() {
        return previousX;
    }

    /**
     * @return la précédante position Y
     */
    public float getPreviousY() {
        return previousY;
    }

    /**
     * Change la valeur X de l'image du segment
     * @param newX valeur X qui remplacera l'ancienne
     */
    public void setX(float newX) {
        segmentImage.setX(newX);
    }

    /**
     * Change la valeur Y de l'image du segment
     * @param newY valeur Y qui remplacera l'ancienne
     */
    public void setY(float newY) {
        segmentImage.setY(newY);
    }

    /**
     * @return la valeur X de l'image du segment
     */
    public float getX() {
        return segmentImage.getX();
    }

    /**
     * @return la valeur Y de l'image du segment
     */
    public float getY() {
        return segmentImage.getY();
    }

    /**
     * @return la largeur de l'image du segment
     */
    public float getWidth() {
        return segmentImage.getWidth();
    }

    /**
     * @return la hauteur de l'image du segment
     */
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