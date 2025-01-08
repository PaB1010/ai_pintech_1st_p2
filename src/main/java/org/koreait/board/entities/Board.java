package org.koreait.board.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseMemberEntity;
import org.koreait.member.constants.Authority;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class Board extends BaseMemberEntity implements Serializable {

    @Id
    @Column(length = 30)
    private String bid;

    @Column(length = 90, nullable = false)
    private String name;

    private boolean open;

    @Lob
    private String category;

    private int rowsPerPage;

    private int pageRanges;

    private int pageRangesMobile;

    private boolean useEditor;

    private boolean useEditorImage;

    private boolean useAttachFile;

    private boolean useComment;


    // 게시글 작성 후 이동 경로
    // list : 목록
    // view : 게시글 조회
    private String locationAfterWriting;

    private String skin;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority listAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority viewAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority writeAuthority;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority commentAuthority;

    @Transient
    private List<String> categories;
}