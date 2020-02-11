package com.walker.config;

import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Config extends com.walker.service.Config {

    public static Pageable turnTo(Page page){
        String order = page.getOrder();
        String[] orders = order.split(" ");

        Sort sort = orders[0].length() > 0
                ? new Sort(orders.length > 1 && orders[1].equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC, orders[0])
                : null;

        //jpa分页从0开始
        Pageable pageable =
                sort == null
                        ? PageRequest.of(page.getNowpage()-1, page.getShownum())
                        : PageRequest.of(page.getNowpage()-1, page.getShownum(), sort);
        return pageable;
    }


}
