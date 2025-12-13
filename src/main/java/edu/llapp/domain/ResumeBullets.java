package edu.llapp.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Resume Item List
 * Transform learning outcomes into resume-ready bullet points
 */
public class ResumeBullets {
    private List<String> items;

    public ResumeBullets() {
        this.items = new ArrayList<>();
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public int size() {
        return items.size();
    }

    /**
     * Export as plain text format
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append("• ").append(items.get(i));
            if (i < items.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ResumeBullets{" + items.size() + " items}";
    }
}