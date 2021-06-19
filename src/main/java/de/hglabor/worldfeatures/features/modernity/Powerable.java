package de.hglabor.worldfeatures.features.modernity;

public interface Powerable {

    void setPower(double power);

    void setInUse(boolean isInuse);

    boolean isInUse();

    double getPower();
}
