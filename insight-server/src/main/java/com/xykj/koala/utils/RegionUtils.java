package com.xykj.koala.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.xykj.koala.vo.Region;
import com.xykj.koala.vo.RegionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Component
public class RegionUtils {

    private static JdbcTemplate jdbcTemplate;

    private static final Cache<String, Region> REGION_CACHE = CacheBuilder.newBuilder().build();

    private static final Map<Long, RegionVO> REGION_MAP = Maps.newHashMap();

    public static List<RegionVO> findChildrenOf(long regionId) {
        return REGION_MAP.values()
                .stream()
                .filter(r -> regionId == r.getParentId())
                .collect(Collectors.toList());
    }

    public static String getRegionName(Long cityId) {
        return REGION_MAP.get(cityId).getRegionName();
    }

    @Autowired
    public void setJdbcTemplate(@Qualifier("koalaUserJdbcTemplate") JdbcTemplate jdbcTemplate) {
        RegionUtils.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    private void selectAllRegions() {
        String sql = "select id,regionCode,regionName,parentId,regionShortnameEN as abbreviation" +
                " from region";
        List<RegionVO> regions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RegionVO.class));
        Map<Long, RegionVO> regionMap = regions.parallelStream().collect(Collectors.toMap(RegionVO::getId, r -> r));
        REGION_MAP.putAll(regionMap);

        regions.parallelStream()
                .peek(r -> r.setParentCode(regionMap.get(r.getParentId() == 0L ? 1L : r.getParentId()).getRegionCode()))
                .forEach(r -> {
                    if (Objects.equals(r.getRegionCode(), RegionVO.COUNTRY_CODE)) {
                        //国家
                        REGION_CACHE.put(r.getRegionCode(), new Region(r.getId(), 0L, 0L, 0L, ""));
                        //找出所有省
                        List<RegionVO> provinces = regions.parallelStream().filter(p -> Objects.equals(p.getParentId(), r.getId())).collect(Collectors.toList());
                        provinces.parallelStream().forEach(p -> {
                            REGION_CACHE.put(p.getRegionCode(), new Region(r.getId(), p.getId(), 0L, 0L, ""));
                            //每个城市
                            List<RegionVO> cities = regions.parallelStream().filter(c -> Objects.equals(c.getParentId(), p.getId())).collect(Collectors.toList());
                            cities.parallelStream().forEach(c -> {
                                REGION_CACHE.put(c.getRegionCode(), new Region(r.getId(), p.getId(), c.getId(), 0L, ""));
                                //每个区
                                List<RegionVO> districts = regions.parallelStream().filter(d -> Objects.equals(d.getParentId(), c.getId())).collect(Collectors.toList());
                                districts.parallelStream().forEach(d -> REGION_CACHE.put(d.getRegionCode(), new Region(r.getId(), p.getId(), c.getId(), d.getId(), "")));
                            });
                        });
                    }
                });
    }


    /**
     * 根据给定的地区代码，返回包含国家、省/直辖市、市/市辖区、区/县的地区ID的地区对象
     * 如果指定的地区代码不是最下一级代码，则返回值的下级id全部为0，如传入的代码表示"山东省"，则返回的市和县的id都为0
     *
     * @param regionCode 地区代码
     * @return 地区对象
     */
    public static Region from(String regionCode) {
        Region region = REGION_CACHE.getIfPresent(regionCode);
        if (isNull(region)) {
            return Region.createDefault();
        }
        return region;
    }

}
