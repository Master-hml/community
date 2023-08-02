package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    public static final String REPLACEMENT = "***";

    // 根节点
    private TreeNode rootNode = new TreeNode();

    @PostConstruct
    public void init(){

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String keyword;
            while((keyword = reader.readLine()) != null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败 ：" + e.getMessage());
        }
//        finally {
//            is.close();
//        }


    }
    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TreeNode tempNode = rootNode;
        for (int i=0; i<keyword.length(); i++){
            char c = keyword.charAt(i);
            TreeNode subNode = tempNode.getSubnode(c);

            if(subNode==null){
                // 初始化子节点
                subNode = new TreeNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指向子节点
            tempNode = subNode;
            // 设置结束标志
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text  待过滤的文本
     * @return  已经过滤的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)) return null;
        StringBuilder sb = new StringBuilder();
        // 指针1
        TreeNode temp = rootNode;
        // 指针2
        int begin = 0;
        //指针3
        int position = 0;

        while(position < text.length()){
            Character c = text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                // 若处于根节点说明可以直接放入
                if(temp == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            // 检查下级节点
            temp = temp.getSubnode(c);
            if(temp == null){
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                temp = rootNode;
                position = ++begin;
            }else if(temp.isKeywordEnd()){
                // 这一段是敏感词，替换掉
                sb.append(REPLACEMENT);
                begin = ++position;
                temp = rootNode;
            }else{
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批加入sb
        sb.append(text.substring(begin));

        return sb.toString();

    }

    private boolean isSymbol(Character c) {
        // 这个范围是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }

//    private String getKeyWord(){
//        StringBuilder sb = new StringBuilder();
//        Map<Character, TreeNode> map = new HashMap<>();
//        TreeNode temp = rootNode;
//        map = temp.getSubNodes();
//        for()
//    }


    // 前缀树
    private class TreeNode{
        // 关键词结束标志
        private boolean isKeywordEnd = false;

        public Map<Character, TreeNode> getSubNodes() {
            return subNodes;
        }

        // 子节点
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keyword) {
            isKeywordEnd = keyword;
        }

        // 添加子节点
        public void addSubNode(Character c, TreeNode node){
            subNodes.put(c, node);
        }
        // 获取子节点
        public TreeNode getSubnode(Character c){
            return subNodes.get(c);
        }
    }




}
