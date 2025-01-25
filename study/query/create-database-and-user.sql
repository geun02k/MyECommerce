
-- 데이터베이스 생성
create database zero_my_e_commerce;

-- 데이터베이스 사용 전용 계정 생성
use mysql;
select user, host from user;

-- 내부접근허용 전용 사용자추가 및 접근권한부여
create user 'myecommerce_user'@'localhost' identified by 'zerobase';
grant all privileges on zero_my_e_commerce.* to 'myecommerce_user'@'localhost';
-- 권한반영
flush privileges;

-- 외부접근허용 전용 사용자추가 및 접근권한부여
create user 'myecommerce_user'@'%' identified by 'zerobase';
grant all privileges on zero_my_e_commerce.* to 'myecommerce_user'@'%';
-- 권한반영
flush privileges;

use zero_my_e_commerce;


