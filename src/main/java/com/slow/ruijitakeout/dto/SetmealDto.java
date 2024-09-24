package com.slow.ruijitakeout.dto;


import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
