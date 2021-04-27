package com.dahuoji.abstractswipelayout;

public interface IHeaderView {
    int getHeaderHeight();

    void move(float dY);

    void loading();

    void complete();
}
