package com.wjrtech.mongo.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class TotalReferenciadosDTO {
    @Field("totalReferenciados")
    List<Integer> totalReferenciadosList;

    public int obterTotalReferenciados() {
        return totalReferenciadosList.get(0); //
    }
    public int obterTotalPaginas(int pageSize) {
//        return (int) Math.ceil((double)totalReferenciadosList.get(0)/pageSize);
        return (int) Math.ceil(totalReferenciadosList.get(0).doubleValue()/pageSize);
    }
}
