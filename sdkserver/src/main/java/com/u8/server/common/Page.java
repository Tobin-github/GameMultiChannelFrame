package com.u8.server.common;

import java.util.List;

/***
 * 分页
 * @param <T>
 */
public class Page<T>
{
  private PageParameter pageParameter;
  private List<T> resultList = null;
  private long totalCount = -1;

  public Page()
  {
    this.pageParameter = new PageParameter(1, 10, false);
  }

  public Page(PageParameter params)
  {
    this.pageParameter = params;
  }

  public long getTotalPages()
  {
    if (this.totalCount == -1)
      return -1;
    long i = this.totalCount / this.pageParameter.getPageSize();
    if (this.totalCount % this.pageParameter.getPageSize() > 0)
      i++;
    return i;
  }

  public List<T> getResultList()
  {
    return this.resultList;
  }

  public void setResultList(List<T> paramList)
  {
    this.resultList = paramList;
  }

  public long getTotalCount()
  {
    return this.totalCount;
  }

  public void setTotalCount(long paramInt)
  {
    this.totalCount = paramInt;
  }

  public PageParameter getPageParameter()
  {
    return this.pageParameter;
  }

  public void setPageParameter(PageParameter params)
  {
    this.pageParameter = params;
  }
}