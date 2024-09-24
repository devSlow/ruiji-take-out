package com.slow.ruijitakeout.dto;

import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
