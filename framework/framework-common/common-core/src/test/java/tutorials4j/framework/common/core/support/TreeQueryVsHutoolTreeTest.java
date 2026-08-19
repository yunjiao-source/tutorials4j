package tutorials4j.framework.common.core.support;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link TreeQuery} 与 Hutool {@link TreeUtil} 构建部门树的对比示例测试。
 *
 * @author Yun Jiao
 */
public class TreeQueryVsHutoolTreeTest {
  private List<Department> departments;

  @BeforeEach
  void beforeEach() {
    departments =
        Arrays.asList(
            new Department(1L, "总公司", 0L, 1),
            new Department(2L, "技术部", 1L, 2),
            new Department(3L, "市场部", 1L, 3),
            new Department(4L, "前端组", 2L, 4),
            new Department(5L, "后端组", 2L, 5),
            new Department(6L, "分公司A", 0L, 6),
            new Department(7L, "销售部", 6L, 7));
  }

  @Test
  void hutoolTree() {
    List<Tree<Long>> deptTree = Department.buildHutoolTree(departments);
    System.out.println(JSONUtil.toJsonPrettyStr(deptTree));
  }

  @Test
  void treeBuilder() {
    TreeQuery<Long, Department> deptTree = Department.buildTreeQuery(departments);

    // 获取所有根节点
    List<Department> rootNodes = deptTree.getAllRootNodes();
    System.out.println("根节点数量: " + rootNodes.size());

    // 获取技术部所有子部门
    List<Department> techChildren = deptTree.getAllChildren(2L, false);
    System.out.println("技术部子部门数量: " + techChildren.size());

    // 获取节点路径
    List<Department> path = deptTree.getParentPath(4L, true);
    String pathStr = path.stream().map(Department::getName).collect(Collectors.joining(" -> "));
    System.out.println("节点4的路径: " + pathStr);
  }

  // 定义部门实体
  /** 部门实体，用于树构建对比演示。 */
  @Data
  static class Department {
    /** 部门 ID。 */
    private Long id;

    /** 部门名称。 */
    private String name;

    /** 父部门 ID。 */
    private Long parentId;

    /** 排序值。 */
    private Integer order;

    /**
     * 构造部门。
     *
     * @param id 部门 ID
     * @param name 部门名称
     * @param parentId 父部门 ID
     * @param order 排序值
     */
    public Department(Long id, String name, Long parentId, Integer order) {
      this.id = id;
      this.name = name;
      this.parentId = parentId;
      this.order = order;
    }

    // 使用TreeQuery
    /**
     * 使用 {@link TreeQuery} 构建部门树查询对象。
     *
     * @param departmentList 部门列表
     * @return 部门树查询对象
     */
    public static TreeQuery<Long, Department> buildTreeQuery(List<Department> departmentList) {
      return new TreeQuery.TreeQueryBuilder<Long, Department>()
          .withNodes(departmentList)
          .withIdGetter(Department::getId)
          .withParentIdGetter(Department::getParentId)
          .withRootId(0L) // 父ID为0的是根节点
          .build();
    }

    // 使用Hutool True
    /**
     * 使用 Hutool {@link TreeUtil} 构建部门树。
     *
     * @param departmentList 部门列表
     * @return 构建出的部门树节点列表
     */
    public static List<Tree<Long>> buildHutoolTree(List<Department> departmentList) {
      return TreeUtil.<Department, Long>build(
          departmentList,
          0L,
          (department, tree) -> {
            tree.setId(department.getId());
            tree.setParentId(department.getParentId());
            tree.setWeight(department.getOrder());
            tree.setName(department.getName());
            tree.putExtra("orginal", department);
          });
    }
  }
}
