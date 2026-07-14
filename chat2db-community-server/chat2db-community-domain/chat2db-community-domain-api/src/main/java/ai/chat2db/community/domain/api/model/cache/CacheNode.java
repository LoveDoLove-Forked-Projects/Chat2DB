package ai.chat2db.community.domain.api.model.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheNode implements Serializable {
    private static final long serialVersionUID = -1L;

    private String name;
    private Map<String, CacheNode> children;

    public CacheNode(String name) {
        this.name = name;
        children = new HashMap<>();
    }

    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    public CacheNode getChild(String name) {
        return children.get(name);
    }

    public void addChild(CacheNode child) {
        children.put(child.getName(), child);
    }

}
