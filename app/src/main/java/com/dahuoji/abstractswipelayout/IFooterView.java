package com.dahuoji.abstractswipelayout;

public interface IFooterView {
    int getFooterHeight();

    void move(float dY);

    void loading();

    void complete();
}
