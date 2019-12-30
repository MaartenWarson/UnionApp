package be.pxl.unionapp.data;

import java.util.List;

import be.pxl.unionapp.domain.Member;

public interface DataStatus {
    void DataIsLoaded(List<Member> members, List<String> keys);
}
