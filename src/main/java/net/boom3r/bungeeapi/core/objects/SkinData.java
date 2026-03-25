package net.boom3r.bungeeapi.core.objects;

public class SkinData {
    private final String value;
    private final String signature;

    /**
     * Crée une nouvelle instance de SkinData.
     *
     * @param value     Base64 représentant la texture du skin.
     * @param signature Signature associée à cette texture (peut être null).
     */
    public SkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    /** Base64 de la texture */
    public String getValue() {
        return value;
    }

    /** Signature Mojang (null si non fournie) */
    public String getSignature() {
        return signature;
    }
}
