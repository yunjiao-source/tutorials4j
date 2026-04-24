package tutorials4j.framework.common.core.util;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link TreeQuery} 单元测试
 *
 * @author Yun Jiao
 */
public class TreeQueryTest {
    // 测试节点类
    @Data
    static class Node {

        final Integer id;
        final Integer parentId;

        final String name;

        Node(Integer id, Integer parentId) {
            this.id = id;
            this.parentId = parentId;
            this.name = "节点-" + id;
        }
    }

    private TreeQuery<Integer, Node> treeQuery;
    private List<Node> allNodes;
    private static final Integer ROOT_ID = 1;

    @BeforeEach
    void setUp() {
        // 构建树结构：
        //        1
        //       / \
        //      2   3
        //     / \   \
        //    4   5   6
        //         \
        //          7
        allNodes = Arrays.asList(
                new Node(1, null),   // 根
                new Node(2, 1),
                new Node(3, 1),
                new Node(4, 2),
                new Node(5, 2),
                new Node(6, 3),
                new Node(7, 5)
        );

        // 根节点判断条件：id == 1
        Predicate<Integer> rootPredicate = ROOT_ID::equals;

        treeQuery = new TreeQuery<>(Node::getId, Node::getParentId, rootPredicate);
        treeQuery.initData(allNodes);
    }

    @Test
    void testGetAllRootNodes() {
        List<Node> roots = treeQuery.getAllRootNodes();
        assertEquals(1, roots.size());
        assertEquals(Integer.valueOf(1), roots.get(0).getId());
    }

    @Test
    void testGetAllChildrenIncludeSelf() {
        List<Node> childrenOf2 = treeQuery.getAllChildren(2, true);
        Set<Integer> expectedIds = new HashSet<>(Arrays.asList(2, 4, 5, 7));
        assertEquals(expectedIds.size(), childrenOf2.size());
        for (Node node : childrenOf2) {
            assertTrue(expectedIds.contains(node.getId()));
        }
    }

    @Test
    void testGetAllChildrenExcludeSelf() {
        List<Node> childrenOf2 = treeQuery.getAllChildren(2, false);
        Set<Integer> expectedIds = new HashSet<>(Arrays.asList(4, 5, 7));
        assertEquals(expectedIds.size(), childrenOf2.size());
        for (Node node : childrenOf2) {
            assertTrue(expectedIds.contains(node.getId()));
        }
    }

    @Test
    void testGetAllChildrenForLeaf() {
        List<Node> childrenOf4 = treeQuery.getAllChildren(4, true);
        assertEquals(1, childrenOf4.size());
        assertEquals(4, childrenOf4.get(0).getId());
        // 排除自身
        List<Node> childrenOf4Exclude = treeQuery.getAllChildren(4, false);
        assertTrue(childrenOf4Exclude.isEmpty());
    }

    @Test
    void testGetParentPathIncludeSelf() {
        List<Node> pathOf7 = treeQuery.getParentPath(7, true);
        // 预期路径: 1 -> 2 -> 5 -> 7
        List<Integer> expectedIds = Arrays.asList(1, 2, 5, 7);
        assertEquals(expectedIds.size(), pathOf7.size());
        for (int i = 0; i < expectedIds.size(); i++) {
            assertEquals(expectedIds.get(i), pathOf7.get(i).getId());
        }
    }

    @Test
    void testGetParentPathExcludeSelf() {
        List<Node> pathOf7 = treeQuery.getParentPath(7, false);
        List<Integer> expectedIds = Arrays.asList(1, 2, 5);
        assertEquals(expectedIds.size(), pathOf7.size());
        for (int i = 0; i < expectedIds.size(); i++) {
            assertEquals(expectedIds.get(i), pathOf7.get(i).getId());
        }
    }

    @Test
    void testGetParentPathForRoot() {
        List<Node> pathOfRoot = treeQuery.getParentPath(1, true);
        assertEquals(1, pathOfRoot.size());
        assertEquals(1, pathOfRoot.get(0).getId());
        // 排除自身
        List<Node> pathOfRootExclude = treeQuery.getParentPath(1, false);
        assertTrue(pathOfRootExclude.isEmpty());
    }

    @Test
    void testGetSubtree() {
        List<Node> subtreeOf2 = treeQuery.getSubtree(2);
        Set<Integer> expectedIds = new HashSet<>(Arrays.asList(2, 4, 5, 7));
        assertEquals(expectedIds.size(), subtreeOf2.size());
        for (Node node : subtreeOf2) {
            assertTrue(expectedIds.contains(node.getId()));
        }
    }

    @Test
    void testIsRootNode() {
        assertTrue(treeQuery.isRootNode(1));
        assertFalse(treeQuery.isRootNode(2));
        assertFalse(treeQuery.isRootNode(7));
    }

    @Test
    void testIsLeafNode() {
        assertTrue(treeQuery.isLeafNode(4));
        assertTrue(treeQuery.isLeafNode(6));
        assertTrue(treeQuery.isLeafNode(7));
        assertFalse(treeQuery.isLeafNode(2));
        assertFalse(treeQuery.isLeafNode(5));
        assertFalse(treeQuery.isLeafNode(1));
    }

    @Test
    void testGetNodeDepth() {
        assertEquals(1, treeQuery.getNodeDepth(1));
        assertEquals(2, treeQuery.getNodeDepth(2));
        assertEquals(2, treeQuery.getNodeDepth(3));
        assertEquals(3, treeQuery.getNodeDepth(4));
        assertEquals(3, treeQuery.getNodeDepth(5));
        assertEquals(3, treeQuery.getNodeDepth(6));
        assertEquals(4, treeQuery.getNodeDepth(7));
    }

    @Test
    void testGetTreeDepth() {
        // 以1为根的树深度为4
        assertEquals(4, treeQuery.getTreeDepth(1));
        // 以2为根的子树深度为3 (2->5->7)
        assertEquals(3, treeQuery.getTreeDepth(2));
        // 以3为根的子树深度为2 (3->6)
        assertEquals(2, treeQuery.getTreeDepth(3));
    }

    @Test
    void testAddNode() {
        // 添加新节点 8 作为 6 的子节点
        Node newNode = new Node(8, 6);
        treeQuery.addNode(newNode);

        assertTrue(treeQuery.isLeafNode(8));
        assertEquals(4, treeQuery.getNodeDepth(8)); // 1->3->6->8
        List<Node> childrenOf6 = treeQuery.getAllChildren(6, false);
        assertEquals(1, childrenOf6.size());
        assertEquals(8, childrenOf6.get(0).getId());

        // 验证路径构建正确
        List<Node> pathOf8 = treeQuery.getParentPath(8, true);
        List<Integer> expectedPath = Arrays.asList(1, 3, 6, 8);
        for (int i = 0; i < expectedPath.size(); i++) {
            assertEquals(expectedPath.get(i), pathOf8.get(i).getId());
        }
    }

    @Test
    void testRemoveLeafNode() {
        // 删除叶子节点 7
        treeQuery.removeNode(7);
        assertFalse(treeQuery.getSubtree(2).stream().anyMatch(n -> n.getId() == 7));
        assertTrue(treeQuery.isLeafNode(5)); // 5 现在成为叶子
        // 路径索引已清理
        List<Node> pathOf7 = treeQuery.getParentPath(7, true);
        assertTrue(pathOf7.isEmpty());
    }

    @Test
    void testRemoveInternalNode() {
        // 删除节点 2，其子节点 4,5,7 应一并删除
        treeQuery.removeNode(2);
        // 验证节点2及其后代都不存在
        Set<Integer> removedIds = new HashSet<>(Arrays.asList(2, 4, 5, 7));
        for (Integer id : removedIds) {
            List<Node> subtree = treeQuery.getSubtree(id);
            assertTrue(subtree.isEmpty(), "节点 " + id + " 应该已被删除");
        }
        // 节点3和6仍存在
        assertFalse(treeQuery.getSubtree(3).isEmpty());
        // 根节点1的子节点列表应该只有3
        List<Node> childrenOf1 = treeQuery.getAllChildren(1, false);
        assertEquals(2, childrenOf1.size());
        assertEquals(3, childrenOf1.get(0).getId());
    }

    @Test
    void testClearAll() {
        treeQuery.clearAll();
        assertTrue(treeQuery.getAllRootNodes().isEmpty());
        assertTrue(treeQuery.getSubtree(1).isEmpty());
        assertTrue(treeQuery.getParentPath(1, true).isEmpty());
    }

    @Test
    void testNonExistentNode() {
        Integer nonExistent = 999;
        assertTrue(treeQuery.getAllChildren(nonExistent, true).isEmpty());
        assertTrue(treeQuery.getParentPath(nonExistent, true).isEmpty());
        assertFalse(treeQuery.isRootNode(nonExistent));
        assertFalse(treeQuery.isLeafNode(nonExistent));
        assertEquals(0, treeQuery.getNodeDepth(nonExistent));
    }

    @Test
    void testCircularReference() {
        // 构建循环引用: A(10) parentId=11, B(11) parentId=10
        List<Node> cyclicNodes = Arrays.asList(
                new Node(10, 11),
                new Node(11, 10)
        );
        Predicate<Integer> rootPredicate = id -> false; // 无根节点
        TreeQuery<Integer, Node> cyclicQuery = new TreeQuery<>(Node::getId, Node::getParentId, rootPredicate);
        cyclicQuery.initData(cyclicNodes);

        // 验证不会无限循环，路径构建应停止
        List<Node> pathOf10 = cyclicQuery.getParentPath(10, true);
        // 由于无根节点且循环，路径只包含自身（或者有限节点）
        // 具体行为：buildNodePathToRoot 检测到 visited 后 break，path 只包含起始节点
        assertEquals(2, pathOf10.size());
        assertEquals(11, pathOf10.get(0).getId());

        List<Node> pathOf11 = cyclicQuery.getParentPath(11, true);
        assertEquals(2, pathOf11.size());
        assertEquals(10, pathOf11.get(0).getId());
    }

    @Test
    void testBuilder() {
        // 使用 Builder 构建 TreeQuery
        TreeQuery<Integer, Node> builtQuery = new TreeQuery.TreeQueryBuilder<Integer, Node>()
                .withNodes(allNodes)
                .withIdGetter(Node::getId)
                .withParentIdGetter(Node::getParentId)
                .withRootId(ROOT_ID)
                .build();

        assertEquals(1, builtQuery.getAllRootNodes().size());
        assertEquals(4, builtQuery.getNodeDepth(7));
        assertEquals(3, builtQuery.getTreeDepth(2));
    }

    @Test
    void testBuilderWithoutNodes() {
        TreeQuery<Integer, Node> emptyQuery = new TreeQuery.TreeQueryBuilder<Integer, Node>()
                .withIdGetter(Node::getId)
                .withParentIdGetter(Node::getParentId)
                .withRootId(ROOT_ID)
                .build();
        assertTrue(emptyQuery.getAllRootNodes().isEmpty());
        emptyQuery.addNode(new Node(1, null));
        assertEquals(1, emptyQuery.getAllRootNodes().size());
    }

    @Test
    void testBuilderMissingIdGetter() {
        assertThrows(IllegalArgumentException.class, () ->
                new TreeQuery.TreeQueryBuilder<Integer, Node>()
                        .withParentIdGetter(Node::getParentId)
                        .withRootId(ROOT_ID)
                        .build()
        );
    }

    @Test
    void testBuilderMissingParentIdGetter() {
        assertThrows(IllegalArgumentException.class, () ->
                new TreeQuery.TreeQueryBuilder<Integer, Node>()
                        .withIdGetter(Node::getId)
                        .withRootId(ROOT_ID)
                        .build()
        );
    }

    @Test
    void testBuilderMissingRootPredicate() {
        assertThrows(IllegalArgumentException.class, () ->
                new TreeQuery.TreeQueryBuilder<Integer, Node>()
                        .withIdGetter(Node::getId)
                        .withParentIdGetter(Node::getParentId)
                        .build()
        );
    }

    @Test
    void testMultipleRoots() {
        // 构建多棵树: 根节点1和根节点8
        List<Node> multiRootNodes = Arrays.asList(
                new Node(1, null),
                new Node(2, 1),
                new Node(8, null),
                new Node(9, 8)
        );
        Predicate<Integer> rootPredicate = id -> id == 1 || id == 8;
        TreeQuery<Integer, Node> multiQuery = new TreeQuery<>(Node::getId, Node::getParentId, rootPredicate);
        multiQuery.initData(multiRootNodes);

        List<Node> roots = multiQuery.getAllRootNodes();
        assertEquals(2, roots.size());
        assertTrue(roots.stream().anyMatch(n -> n.getId() == 1));
        assertTrue(roots.stream().anyMatch(n -> n.getId() == 8));

        assertEquals(2, multiQuery.getNodeDepth(2));
        assertEquals(2, multiQuery.getNodeDepth(9));
        assertEquals(2, multiQuery.getTreeDepth(1));
        assertEquals(2, multiQuery.getTreeDepth(8));
    }
}
