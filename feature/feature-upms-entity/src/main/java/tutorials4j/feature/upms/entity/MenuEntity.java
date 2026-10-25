package tutorials4j.feature.upms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Entity
@Table(
    name = "upms_menu",
    indexes = {@Index(name = "upms_menu_idx1", columnList = "parent_id")})
public class MenuEntity extends UpmsEntity {
  @Column(name = "parent_id", insertable = false, updatable = false)
  private String parentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "upms_menu_fk1"))
  private MenuEntity parent;

  @Column private String path;

  @Column private String name;

  @Column private String component;

  /** */
  @Column private String redirect;

  /** 类型：link-链接，embedded-内嵌，catalog-目录， button-按钮， menu-菜单 */
  @Column
  @Enumerated(EnumType.STRING)
  private MenuType type;

  /** 激活图标（菜单）; 用于配置页面的激活图标，会在菜单中显示。一般会配合图标库使用，如果是http链接，会自动加载图片 */
  @Column private String activeIcon;

  /** 当前激活的菜单，有时候不想激活现有菜单，需要激活父级菜单时使用; 用于配置当前激活的菜单，有时候页面没有显示在菜单内，需要激活父级菜单时使用。 */
  @Column private String activePath;

  /**
   * 是否使用 activePath 对应的路由层级生成面包屑 默认 false; 设置为 true 时，面包屑会使用 activePath
   * 对应的路由层级补充当前路由的匹配记录。适用于详情页等未嵌套在目标菜单路由下，但仍需要展示完整面包屑层级的页面。
   */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum breadcrumbUseActivePath;

  /** 是否固定标签页;用 于配置页面是否固定标签页，固定后页面不可关闭。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum affixTab;

  /** 固定标签页的顺序; 用于配置页面固定标签页的排序, 采用升序排序。 默认 0 */
  @Column private Integer affixTabOrder;

  /** 需要特定的角色标识才可以访问; 用于配置页面的权限，只有拥有对应权限的用户才能访问页面，不配置则不需要权限。 默认 [] */
  @Column private String authority;

  /** 徽标; 用于配置页面的徽标，会在菜单显示。 */
  @Column private String badge;

  /** 徽标类型: 'dot' | 'normal'; 用于配置页面的徽标类型，dot 为小红点，normal 为文本。 */
  @Column private String badgeType;

  /** 徽标颜色: 'default' | 'destructive' | 'primary' | 'success' | 'warning'; 用于配置页面的徽标颜色。 */
  @Column private String badgeVariants;

  /** 路由的完整路径作为key（默认true）; 是否将路由的完整路径作为tab key（默认true） */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum fullPathKey;

  /** 当前路由的子级在菜单中不展现; 用于配置页面的子页面是否在菜单中隐藏，隐藏后子页面不会在菜单中显示。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum hideChildrenInMenu;

  /** 当前路由在面包屑中不展现; 用于配置页面是否在面包屑中隐藏，隐藏后页面不会在面包屑中显示。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum hideInBreadcrumb;

  /** 当前路由在菜单中不展现; 用于配置页面是否在菜单中隐藏，隐藏后页面不会在菜单中显示。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum hideInMenu;

  /** 当前路由在标签页不展现; 用于配置页面是否在标签页中隐藏，隐藏后页面不会在标签页中显示。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum hideInTab;

  /** 图标（菜单/tab）; 用于配置页面的图标，会在菜单和标签页中显示。一般会配合图标库使用，如果是http链接，会自动加载图片。 */
  @Column private String icon;

  /** iframe 地址; 用于配置内嵌页面的 iframe 地址，设置后会在当前页面内嵌对应的页面。 */
  @Column private String iframeSrc;

  /** 忽略权限，直接可以访问; 用于配置页面是否忽略权限，直接可以访问。 默认 false */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum ignoreAccess;

  /** 开启KeepAlive缓存，默认false; 用于配置页面是否开启缓存，开启后页面会缓存，不会重新加载，仅在标签页启用时有效。 */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum keepAlive;

  /** 外链-跳转路径; 用于配置外链跳转路径，会在新窗口打开。 */
  @Column private String link;

  /** 标签页最大打开数量; 用于配置标签页最大打开数量，设置后会在打开新标签页时自动关闭最早打开的标签页(仅在打开同名标签页时生效)。 默认 false */
  @Column private Integer maxNumOfOpenTab;

  /** 菜单可以看到，但是访问会被重定向到403; 用于配置页面在菜单可以看到，但是访问会被重定向到403。 */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum menuVisibleWithForbidden;

  /**
   * 当前路由不使用基础布局（仅在顶级生效）;
   * 用于配置当前路由不使用基础布局，仅在顶级时生效。默认情况下，所有的路由都会被包裹在基础布局中（包含顶部以及侧边等导航部件），如果你的页面不需要这些部件，可以设置 noBasicLayout
   * 为 true。
   */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum noBasicLayout;

  /** 在新窗口打开; 设置为 true 时，会在新窗口打开页面。 */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum openInNewWindow;

  /** 用于路由->菜单排序; 用于配置页面的排序，用于路由到菜单排序。 注意: 排序仅针对一级菜单有效，二级菜单的排序需要在对应的一级菜单中按代码顺序设置。 */
  @Column private Integer order;

  /** 菜单所携带的参数; 用于配置页面的菜单参数，会在菜单中传递给页面。 */
  @Column private String query;

  /** 标题名称 */
  @Column private String title;

  /**
   * 用于配置当前路由是否要将route对应dom元素缓存起来。对于一些复杂页面切换tab浏览器回流/重绘会导致卡顿， domCached 设为
   * true可解决该问题，但是也有代价：1、内存占用升高 2、vue的部分生命周期不会触发
   */
  @Column
  @Enumerated(EnumType.STRING)
  private YesNoEnum domCached;

  @OneToMany(mappedBy = "parent")
  @OrderBy("path ASC")
  private List<MenuEntity> children = new ArrayList<>();

  @ManyToMany(mappedBy = "menus")
  private Set<RoleEntity> roles = new HashSet<>();
}
