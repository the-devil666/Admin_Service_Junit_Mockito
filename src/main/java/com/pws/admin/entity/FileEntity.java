package com.pws.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
public class FileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_data", length = 100000)
    private byte[] fileData;
    
    // getters and setters
}
