package tutorials4j.framework.core.lang;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 通用树形结构查询工具
 * 支持多棵树和任意节点
 *
 * @param <ID> 节点ID类型
 * @param <T> 节点对象类型
 * @author Yun Jiao
 */
public class TreeQuery<ID, T> {
    // 核心数据
    private final List<T> allNodes = new ArrayList<>();

    // 索引结构
    private final Map<ID, T> idToNodeMap = new HashMap<>();
    private final Map<ID, List<T>> parentToChildrenMap = new HashMap<>();
    private final Map<ID, List<T>> childToParentPathMap = new HashMap<>();

    // 字段获取器
    private final Function<T, ID> idGetter;
    private final Function<T, ID> parentIdGetter;

    // 根节点判断条件
    private final Predicate<ID> isRootIdPredicate;

    /**
     * 构造函数
     *
     * @param idGetter ID获取器
     * @param parentIdGetter 父ID获取器
     * @param rootIdPredicate 根节点判断条件
     */
    public TreeQuery(Function<T, ID> idGetter,
                              Function<T, ID> parentIdGetter,
                              Predicate<ID> rootIdPredicate) {
        this.idGetter = idGetter;
        this.parentIdGetter = parentIdGetter;
        this.isRootIdPredicate = rootIdPredicate;
    }

    /**
     * 初始化数据
     *
     * @param nodes 所有节点
     */
    public void initData(List<T> nodes) {
        clearAll();
        allNodes.addAll(nodes);
        buildAllIndexes();
    }

    /**
     * 构建所有索引
     */
    private void buildAllIndexes() {
        // 1. 构建ID到节点的映射
        for (T node : allNodes) {
            ID id = idGetter.apply(node);
            idToNodeMap.put(id, node);
        }
        // 2. 构建父节点到子节点列表的映射
        for (T node : allNodes) {
            ID parentId = parentIdGetter.apply(node);
            parentToChildrenMap
                    .computeIfAbsent(parentId, k -> new ArrayList<>())
                    .add(node);
        }
        // 3. 为每个节点构建到根节点的路径
        for (T node : allNodes) {
            ID nodeId = idGetter.apply(node);
            buildNodePathToRoot(nodeId);
        }
    }

    /**
     * 构建节点到根节点的路径
     */
    private void buildNodePathToRoot(ID nodeId) {
        List<T> path = new ArrayList<>();

        ID currentId = nodeId;
        Set<ID> visited = new HashSet<>();  // 防止循环引用

        while (currentId != null) {
            if (visited.contains(currentId)) {
                break;  // 检测到循环引用
            }
            visited.add(currentId);

            T currentNode = idToNodeMap.get(currentId);
            if (currentNode == null) {
                break;
            }
            // 添加到路径开头
            path.add(0, currentNode);
            // 如果是根节点，停止向上查找
            if (isRootIdPredicate.test(currentId)) {
                break;
            }
            // 获取父节点ID
            ID parentId = parentIdGetter.apply(currentNode);
            // 父节点为null或与当前节点相同，停止
            if (parentId == null || parentId.equals(currentId)) {
                break;
            }
            currentId = parentId;
        }
        childToParentPathMap.put(nodeId, path);
    }

    /**
     * 获取所有根节点
     *
     * @return 所有根节点列表
     */
    public List<T> getAllRootNodes() {
        return allNodes.stream()
                .filter(node -> isRootIdPredicate.test(idGetter.apply(node)))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定节点的所有子节点（包括间接子节点）
     *
     * @param targetId 目标节点ID
     * @param includeSelf 是否包含自身
     * @return 所有子节点列表
     */
    public List<T> getAllChildren(ID targetId, boolean includeSelf) {
        if (!idToNodeMap.containsKey(targetId)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();

        // 如果包含自身，先添加自身
        if (includeSelf) {
            result.add(idToNodeMap.get(targetId));
        }

        // 使用队列进行广度优先遍历
        Queue<ID> queue = new LinkedList<>();
        queue.offer(targetId);

        while (!queue.isEmpty()) {
            ID currentId = queue.poll();
            List<T> children = parentToChildrenMap.get(currentId);

            if (children != null) {
                for (T child : children) {
                    result.add(child);
                    queue.offer(idGetter.apply(child));
                }
            }
        }

        return result;
    }

    /**
     * 获取节点路径（从父节点到根节点）
     *
     * @param targetId 目标节点ID
     * @param includeSelf 是否包含自身
     * @return 节点路径列表
     */
    public List<T> getParentPath(ID targetId, boolean includeSelf) {
        if (!childToParentPathMap.containsKey(targetId)) {
            return Collections.emptyList();
        }

        List<T> path = new ArrayList<>(childToParentPathMap.get(targetId));

        // 如果不包含自身，移除最后一个元素（自身）
        if (!includeSelf && !path.isEmpty()) {
            path.remove(path.size() - 1);
        }

        return path;
    }

    /**
     * 获取子树（以指定节点为根的子树）
     *
     * @param targetId 目标节点ID
     * @return 子树的所有节点
     */
    public List<T> getSubtree(ID targetId) {
        return getAllChildren(targetId, true);
    }

    /**
     * 判断节点是否为根节点
     *
     * @param nodeId 节点ID
     * @return 是否为根节点
     */
    public boolean isRootNode(ID nodeId) {
        return isRootIdPredicate.test(nodeId);
    }

    /**
     * 判断节点是否为叶子节点
     *
     * @param nodeId 节点ID
     * @return 是否为叶子节点
     */
    public boolean isLeafNode(ID nodeId) {
        if (!idToNodeMap.containsKey(nodeId)) {
            return false;
        }
        List<T> children = parentToChildrenMap.get(nodeId);
        return children == null || children.isEmpty();
    }

    /**
     * 获取节点的深度（从根节点开始计算）
     *
     * @param nodeId 节点ID
     * @return 节点深度，根节点为1
     */
    public int getNodeDepth(ID nodeId) {
        List<T> path = getParentPath(nodeId, true);
        return path.size();
    }

    /**
     * 获取树的深度
     *
     * @param rootId 根节点ID
     * @return 树的深度
     */
    public int getTreeDepth(ID rootId) {
        int maxDepth = 0;
        int minDepth = Integer.MAX_VALUE;
        List<T> allNodesInTree = getAllChildren(rootId, true);

        for (T node : allNodesInTree) {
            ID nodeId = idGetter.apply(node);
            int depth = getNodeDepth(nodeId);
            maxDepth = Math.max(maxDepth, depth);
            minDepth = Math.min(minDepth, depth);
        }

        return maxDepth - minDepth + 1;
    }

    /**
     * 添加节点
     *
     * @param node 要添加的节点
     */
    public void addNode(T node) {
        allNodes.add(node);

        ID id = idGetter.apply(node);
        ID parentId = parentIdGetter.apply(node);

        // 更新索引
        idToNodeMap.put(id, node);
        parentToChildrenMap
                .computeIfAbsent(parentId, k -> new ArrayList<>())
                .add(node);

        // 重新构建路径索引
        buildNodePathToRoot(id);
    }

    /**
     * 删除节点及其所有后代
     *
     * @param nodeId 要删除的节点ID
     */
    public void removeNode(ID nodeId) {
        T node = idToNodeMap.get(nodeId);
        if (node == null) {
            return;
        }

        // 获取所有后代节点
        List<T> allDescendants = getAllChildren(nodeId, false);

        // 删除节点自身
        allNodes.remove(node);
        idToNodeMap.remove(nodeId);
        childToParentPathMap.remove(nodeId);

        // 从父节点的子节点列表中移除
        ID parentId = parentIdGetter.apply(node);
        if (parentToChildrenMap.containsKey(parentId)) {
            parentToChildrenMap.get(parentId).removeIf(n ->
                    idGetter.apply(n).equals(nodeId));
        }

        // 删除所有后代节点
        for (T descendant : allDescendants) {
            ID descendantId = idGetter.apply(descendant);
            allNodes.remove(descendant);
            idToNodeMap.remove(descendantId);
            childToParentPathMap.remove(descendantId);
        }
    }

    /**
     * 清空所有数据
     */
    public void clearAll() {
        allNodes.clear();
        idToNodeMap.clear();
        parentToChildrenMap.clear();
        childToParentPathMap.clear();
    }

    /**
     *
     * 树查询构建器
     *
     * @param <ID>
     * @param <T>
     */
    public static class TreeQueryBuilder<ID, T> {

        private List<T> nodes = new ArrayList<>();
        private Function<T, ID> idGetter;
        private Function<T, ID> parentIdGetter;
        private Predicate<ID> rootIdPredicate;

        /**
         * 设置节点列表
         */
        public TreeQueryBuilder<ID, T> withNodes(List<T> nodes) {
            this.nodes = nodes != null ? new ArrayList<>(nodes) : new ArrayList<>();
            return this;
        }

        /**
         * 设置ID获取器
         */
        public TreeQueryBuilder<ID, T> withIdGetter(Function<T, ID> idGetter) {
            this.idGetter = idGetter;
            return this;
        }

        /**
         * 设置父ID获取器
         */
        public TreeQueryBuilder<ID, T> withParentIdGetter(Function<T, ID> parentIdGetter) {
            this.parentIdGetter = parentIdGetter;
            return this;
        }

        /**
         * 设置根节点判断条件（ID为特定值）
         */
        public TreeQueryBuilder<ID, T> withRootId(ID rootIdValue) {
            this.rootIdPredicate = id -> rootIdValue == null ?
                    id == null : rootIdValue.equals(id);
            return this;
        }

        /**
         * 设置自定义根节点判断条件
         */
        public TreeQueryBuilder<ID, T> withRootPredicate(Predicate<ID> rootIdPredicate) {
            this.rootIdPredicate = rootIdPredicate;
            return this;
        }

        /**
         * 构建树查询器
         */
        public TreeQuery<ID, T> build() {
            if (idGetter == null || parentIdGetter == null) {
                throw new IllegalArgumentException("ID获取器和父ID获取器不能为空");
            }

            if (rootIdPredicate == null) {
                throw new IllegalArgumentException("根节点判断条件不能为空");
            }

            TreeQuery<ID, T> treeQuery = new TreeQuery<>(idGetter, parentIdGetter, rootIdPredicate);

            if (!nodes.isEmpty()) {
                treeQuery.initData(nodes);
            }

            return treeQuery;
        }
    }
}
