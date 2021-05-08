package cn.mxy.pojo;

import java.util.List;

public class PageBean {
    private List<ResultBean> data; //获取的完整表
    private Long total; //表中一共有多少条数据
    private Integer pageSize = 10; //每页最多有多少条，默认10条，用户传入
    private Integer currentPage = 1; //当前页为第几页，默认为第一页，用户传入
    private Integer totalPage; //尾页（该表总共的页数），需要计算

    private Integer beginPage; //需要展示的第一页标号
    private Integer endPage; //需要展示的最后一页页标号

    public List<ResultBean> getData() {
        return data;
    }

    public void setData(List<ResultBean> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getBeginPage() {
        return beginPage;
    }

    public void setBeginPage(Integer beginPage) {
        this.beginPage = beginPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }
}
