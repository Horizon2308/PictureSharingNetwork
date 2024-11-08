package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.tag LIKE %:keyword% OR p.description LIKE %:keyword% " +
            "OR p.userId.nickname LIKE %:keyword% OR p.userId.fullName LIKE %:keyword%)" +
            " AND (p.shareFor = 1 OR p.shareFor = 2)")
    Page<Post> searchPosts(
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.tag LIKE %:keyword% OR p.description LIKE %:keyword% " +
            "OR p.userId.nickname LIKE %:keyword% OR p.userId.fullName LIKE %:keyword%)" +
            " AND (p.shareFor = 1 OR p.shareFor = 2) AND (p.userId.id IN " +
            "(SELECT f.followedId FROM Follow f WHERE f.followerId = :userId))")
    Page<Post> searchPostsByFollowedUser(
            @Param("keyword") String keyword,
            @Param("user_id") Long userId,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.tag LIKE %:keyword% OR p.description LIKE %:keyword%)" +
            "AND p.userId = :userId")
    Page<Post> searchPostsForOwnProfileUser (
            @Param("keyword") String keyword,
            @Param("user_id") Long userId,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.tag LIKE %:keyword% OR p.description LIKE %:keyword%)" +
            "AND p.userId = :userId AND (p.shareFor = 1 OR p.shareFor = 2)")
    Page<Post> searchPostsByUserId (
            @Param("keyword") String keyword,
            @Param("user_id") Long userId,
            Pageable pageable);
}
