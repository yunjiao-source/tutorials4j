package tutorials4j.framework.common.core;

/**
 * 符号与常用字符串常量定义。
 *
 * <p>集中定义标点符号、空白字符、SQL 关键字、文件后缀及 XML/HTML 片段等常用字符串常量，供项目内统一引用。
 *
 * @author Yun Jiao
 */
public interface SymbolConsts {
  /** 与号 */
  String AMPERSAND = "&";

  /** HTML 转义后的与号 */
  String AMPERSAND_ENCODED = "&amp;";

  /** 单引号 */
  String APOSTROPHE = "'";

  /** 单引号加逗号 */
  String APOSTROPHE_AND_COMMA = "',";

  /** 单引号、逗号、单引号 */
  String APOSTROPHE_AND_COMMA_AND_APOSTROPHE = "','";

  /**
   * @ 符号
   */
  String AT = "@";

  /** 反斜杠 */
  String BACK_SLASH = "\\";

  /** SQL 关键字 BETWEEN */
  String BETWEEN = "BETWEEN";

  /** 空字符串 */
  String BLANK = "";

  /** CDATA 开始标记 */
  String CDATA_OPEN = "<![CDATA[";

  /** CDATA 结束标记 */
  String CDATA_CLOSE = "]]>";

  /** 右方括号 */
  String CLOSE_BRACKET = "]";

  /** 右花括号 */
  String CLOSE_CURLY_BRACE = "}";

  /** 右圆括号 */
  String CLOSE_PARENTHESIS = ")";

  /** 冒号 */
  String COLON = ":";

  /** 逗号 */
  String COMMA = ",";

  /** 逗号加单引号 */
  String COMMA_AND_APOSTROPHE = ",'";

  /** 逗号加空格 */
  String COMMA_AND_SPACE = ", ";

  /** 分号加空格 */
  String SEMICOLON_AND_SPACE = "; ";

  /** 短横线 */
  String DASH = "-";

  /** 两个单引号 */
  String DOUBLE_APOSTROPHE = "''";

  /** 两个右方括号 */
  String DOUBLE_CLOSE_BRACKET = "]]";

  /** 两个左方括号 */
  String DOUBLE_OPEN_BRACKET = "[[";

  /** 双斜杠 */
  String DOUBLE_SLASH = "//";

  /** 等号 */
  String EQUAL = "=";

  /** 大于号 */
  String GREATER_THAN = ">";

  /** 大于等于号 */
  String GREATER_THAN_OR_EQUAL = ">=";

  /** 正斜杠 */
  String FORWARD_SLASH = "/";

  /** 四个空格 */
  String FOUR_SPACES = "    ";

  /** 闭合标签左尖括号 */
  String FINISH_LEFT_ANGLE = "</";

  /** 自闭合标签右尖括号 */
  String FINISH_RIGHT_ANGLE = "/>";

  /** GBK 字符集名称 */
  String GBK = "GBK";

  /** SQL 关键字 IS NOT NULL */
  String IS_NOT_NULL = "IS NOT NULL";

  /** SQL 关键字 IS NULL */
  String IS_NULL = "IS NULL";

  /** SQL 关键字 IN */
  String IN = "IN";

  /** 左尖括号 */
  String LEFT_ANGLE = "<";

  /** 小于号 */
  String LESS_THAN = "<";

  /** 小于等于号 */
  String LESS_THAN_OR_EQUAL = "<=";

  /** SQL 关键字 LIKE */
  String LIKE = "LIKE";

  /** 减号 */
  String MINUS = "-";

  /** 不间断空格 HTML 实体 */
  String NBSP = "&nbsp;";

  /** 换行符 */
  String NEW_LINE = "\n";

  /** 不等于号 */
  String NOT_EQUAL = "!=";

  /** SQL 中的不等于号 */
  String DB_NOT_EQUAL = "<>";

  /** SQL 关键字 NOT LIKE */
  String NOT_LIKE = "NOT LIKE";

  /** null 字符串 */
  String NULL = "null";

  /** 左方括号 */
  String OPEN_BRACKET = "[";

  /** 左花括号 */
  String OPEN_CURLY_BRACE = "{";

  /** 左圆括号 */
  String OPEN_PARENTHESIS = "(";

  /** 百分号 */
  String PERCENT = "%";

  /** 句点 */
  String PERIOD = ".";

  /** 竖线 */
  String PIPE = "|";

  /** 加号 */
  String PLUS = "+";

  /** 井号 */
  String POUND = "#";

  /** 问号 */
  String QUESTION = "?";

  /** 双引号 */
  String QUOTE = "\"";

  /** 回车符 */
  String RETURN = "\r";

  /** 回车换行符 */
  String RETURN_NEW_LINE = "\r\n";

  /** 右尖括号 */
  String RIGHT_ANGLE = ">";

  /** 分号 */
  String SEMICOLON = ";";

  /** 斜杠（与正斜杠相同） */
  String SLASH = FORWARD_SLASH;

  /** 空格 */
  String SPACE = " ";

  /** 星号 */
  String STAR = "*";

  /** 制表符 */
  String TAB = "\t";

  /** 波浪号 */
  String TILDE = "~";

  /** 下划线 */
  String UNDERLINE = "_";

  /** 数字零 */
  String ZERO = "0";

  /** Excel 2003 文件后缀 */
  String SUFFIX_EXCEL_2003 = ".xls";

  /** Excel 2007 文件后缀 */
  String SUFFIX_EXCEL_2007 = ".xlsx";

  /** JPEG 图片文件后缀 */
  String SUFFIX_JPEG = ".jpg";

  /** XML 文件后缀 */
  String SUFFIX_XML = ".xml";

  /** PDF 文件后缀 */
  String SUFFIX_PDF = ".pdf";

  /** ZIP 压缩文件后缀 */
  String SUFFIX_ZIP = ".zip";

  /** Word 2003 文件后缀 */
  String SUFFIX_DOC = ".doc";

  /** Word 2007 文件后缀 */
  String SUFFIX_DOCX = ".docx";

  /** PowerPoint 2003 文件后缀 */
  String SUFFIX_PPT = ".ppt";

  /** PowerPoint 2007 文件后缀 */
  String SUFFIX_PPTX = ".pptx";

  /** Excel 文件后缀 */
  String SUFFIX_EXCEL = ".xls";

  /** Excel 新版文件后缀 */
  String SUFFIX_EXCELX = ".xlsx";

  /** Flash 文件后缀 */
  String SUFFIX_SWF = ".swf";

  /** 属性文件后缀 */
  String SUFFIX_PROPERTIES = ".properties";

  /** YML 配置文件后缀 */
  String SUFFIX_YML = ".yml";

  /** YAML 配置文件后缀 */
  String SUFFIX_YAML = ".yaml";

  /** JSON 文件后缀 */
  String SUFFIX_JSON = ".json";

  /** XML 声明片段 */
  String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
}
