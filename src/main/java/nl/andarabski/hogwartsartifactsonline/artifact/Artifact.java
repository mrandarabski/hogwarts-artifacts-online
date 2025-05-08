package nl.andarabski.hogwartsartifactsonline.artifact;

import jakarta.persistence.*;
import nl.andarabski.hogwartsartifactsonline.wizard.Wizard;


import java.io.Serializable;

@Entity
public class Artifact implements Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Wizard owner;


    public Artifact() {}

    public Artifact(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Wizard getOwner() {
        return owner;
    }
    public void setOwner(Wizard owner) {
        this.owner = owner;
    }
}
