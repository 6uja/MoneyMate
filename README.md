# MoneyMate

## 프로젝트 소개

MoneyMate는 JavaFX와 SQLite를 활용하여 개발한 개인 가계부 및 소비 분석 프로그램이다.

사용자는 수입과 지출 내역을 관리할 수 있으며, 월별 수입·지출·잔액 확인, 카테고리별 소비 통계, 예산 관리 기능을 통해 자신의 소비 패턴을 분석할 수 있다.

---

## 주요 기능

* 수입 및 지출 등록
* 거래 내역 조회 및 삭제
* 월별 수입, 지출, 잔액 계산
* 카테고리별 소비 통계
* 예산 설정 및 초과 경고
* CSV 데이터 가져오기 및 내보내기
* SQLite 데이터 저장

---

## 사용 기술

### Language

* Java 25

### GUI

* JavaFX

### Database

* SQLite
* JDBC

### Design Pattern

* MVC Pattern
* DAO Pattern

### Java Advanced Features

* Stream API
* Lambda Expression
* Enum
* Optional
* LocalDate API
* Collections Framework
* Exception Handling

### File Processing

* CSV Import / Export
* File I/O

---

## 프로젝트 구조

```text
MoneyMate
│
├─ src
│   ├─ app
│   ├─ dao
│   ├─ db
│   ├─ model
│   ├─ service
│   ├─ util
│   └─ Main.java
│
├─ sample_transactions.csv
├─ images
├─ moneymate.db
└─ README.md
```

---

## 실행 방법

1. JavaFX SDK 연결
2. SQLite JDBC 추가
3. Main.java 실행

---

## 기대 효과

본 프로젝트를 통해 JavaFX GUI 프로그래밍, SQLite 데이터베이스 연동, 객체지향 설계, MVC 패턴, DAO 패턴 및 파일 입출력 기능을 종합적으로 활용하였다.

실제 가계부 프로그램과 유사한 기능을 구현하여 Java 응용 프로그램 개발 역량을 향상시킬 수 있었다.
