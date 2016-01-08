package com.quanliren.quan_one.listener;

/**
 * @author: kongyunli
 * @Description:
 * @Date: 2015/12/2 16:22
 */
public interface ICheckBoxInterface {

    /**
     * item是否选中
     *
     * @param position  item的绝对位置(源数据list中的位置)
     * @param isChecked item的选中状态
     */
    void checkChild(int position, boolean isChecked);

}
