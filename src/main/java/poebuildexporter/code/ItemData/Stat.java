package poebuildexporter.code.ItemData;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Stat implements Serializable {


    public static final Set<String> weaponDpsStatNames = Set.of(
            "physical damage",
            "fire damage",
            "cold damage",
            "lightning damage",
            "increased physical damage",
            "increased attack speed"
    );

    public static final List<String> statStringsToIgnoreWhenParsing = List.of(
            "uniq",
            "impl",
            "sock",
            "qual",
            "rune",
            "leve",
            "item"
    );

	private static final long serialVersionUID = 1000L;
	private String text;
	private String id;
	private String alternateId;
	private double roll;
	
	public Stat(String text, double roll) {
		this.roll = roll;
		this.text = text;
	}
	
	
	public Stat(String text, String id) {
		this(text, id, 0);
	}
	
	public Stat(String text, String id, double roll) {
		this.text = text;
		this.roll = roll;
		this.id = id;
	}

    public static boolean isLocalDefenseStat(String statLine) {
        for(String localDefenseStat : Item.defenseSetterMap.keySet()) {
            if(statLine.toLowerCase().trim().startsWith(localDefenseStat)) {
                return true;
            }
        }
        return false;
    }
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public double getRoll() {
		return roll;
	}
	
	public String getId() {
		return id;
	}

	public String getAlternateId() {
		return alternateId;
	}

	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}
	
	public String toString() {
		return text;
	}

}
