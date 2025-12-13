package edu.llapp.strategies.recommenders;

import edu.llapp.domain.CourseListWithReasons;
import edu.llapp.domain.GapReport;
import edu.llapp.domain.UserProfile;
import edu.llapp.domain.Course;
import java.util.List;

/**
 * 課程推薦策略介面
 */
public interface ICourseStrategy {
    /**
     * 策略類型
     */
    String getAlgorithmType();

    /**
     * 執行課程推薦
     * @param gap 技能差距報告
     * @param profile 使用者檔案
     * @param availableCourses 可用課程列表
     * @return 推薦課程及原因
     */
    CourseListWithReasons recommend(GapReport gap, UserProfile profile, List<Course> availableCourses);
}