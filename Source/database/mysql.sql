USE mydatabase

CREATE TABLE users (
	user_id NVARCHAR(255),
	avatar_url NVARCHAR(255),
    email NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    is_active BIT(1) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    phone_number NVARCHAR(255) NOT NULL,
    role NVARCHAR(255) NOT NULL,
    username NVARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
)

CREATE TABLE tags (
	tag_id NVARCHAR(255),
    name NVARCHAR(255) NOT NULL,
    PRIMARY KEY (tag_id)
)

CREATE TABLE summaries (
	summary_id NVARCHAR(255),
    approved_at TIMESTAMP NOT NULL,
    content MEDIUMTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    grade NVARCHAR(255) NOT NULL,
    image_url NVARCHAR(255),
    method NVARCHAR(255) NOT NULL,
    read_count INT NOT NULL,
    status NVARCHAR(255),
    summary_content MEDIUMTEXT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    created_by NVARCHAR(255) NOT NULL,
    PRIMARY KEY (summary_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
)

CREATE TABLE summary_session (
	session_id BIGINT,
    content MEDIUMTEXT NOT NULL,
    created_by NVARCHAR(255),
    content_hash NVARCHAR(255) NOT NULL,
    timestamp NVARCHAR(255) NOT NULL,
    PRIMARY KEY (session_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
)

CREATE TABLE summary_history (
	history_id BIGINT,
    method NVARCHAR(255),
    summary_content MEDIUMTEXT NOT NULL,
    session_id BIGINT,
    is_accepted BIT(1),
    image_url NVARCHAR(255),
    PRIMARY KEY (history_id),
    FOREIGN KEY (session_id) REFERENCES summary_session(session_id) ON UPDATE CASCADE ON DELETE CASCADE
)

CREATE TABLE read_history (
	id BIGINT,
    summary_id NVARCHAR(255),
    user_id NVARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (summary_id) REFERENCES summaries(summary_id) ON UPDATE CASCADE ON DELETE CASCADE
)

CREATE TABLE summary_tags (
	summary_tag_id NVARCHAR(255),
    summary_id NVARCHAR(255),
    tag_id NVARCHAR(255),
    PRIMARY KEY (summary_tag_id),
    FOREIGN KEY (summary_id) REFERENCES summaries(summary_id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON UPDATE CASCADE ON DELETE CASCADE
)

CREATE TABLE conversations (
	message_id NVARCHAR(255),
    user_id NVARCHAR(255),
    role NVARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE
)

ALTER TABLE read_history MODIFY id BIGINT