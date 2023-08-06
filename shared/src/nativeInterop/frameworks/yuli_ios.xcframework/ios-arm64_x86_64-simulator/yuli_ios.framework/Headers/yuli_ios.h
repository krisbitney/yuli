// Types.h

#import <Foundation/Foundation.h>

@class User;
@class Profile;
@protocol SocialApi;
@class KeychainItem;

@interface User: NSObject

@property (nonatomic, readonly) NSString* username;
@property (nonatomic, readonly, nullable) NSString* name;
@property (nonatomic, readonly, nullable) NSString* picUrl;
@property (nonatomic, readonly) NSInteger followerCount;
@property (nonatomic, readonly) NSInteger followingCount;
@property (nonatomic, readonly) NSInteger mediaCount;

- (instancetype)initWithUsername:(NSString*)username name:(nullable NSString*)name picUrl:(nullable NSString*)picUrl followerCount:(NSInteger)followerCount followingCount:(NSInteger)followingCount mediaCount:(NSInteger)mediaCount;

@end

@interface Profile: NSObject

@property (nonatomic, readonly) NSString* username;
@property (nonatomic, readonly, nullable) NSString* name;
@property (nonatomic, readonly, nullable) NSString* picUrl;

- (instancetype)initWithUsername:(NSString*)username name:(nullable NSString*)name picUrl:(nullable NSString*)picUrl;

@end

@protocol SocialApi <NSObject>

- (void)login:(NSString*)username password:(NSString*)password completion:(void (^)(BOOL, NSString* _Nullable))completion;
- (void)restoreSession:(void (^)(BOOL, NSString* _Nullable))completion;
- (void)fetchUserProfile:(void (^)(User* _Nullable, NSString* _Nullable))completion;
- (void)fetchFollowers:(int64_t)pageDelay completion:(void (^)(NSArray<Profile*>* _Nullable, NSString* _Nullable))completion;
- (void)fetchFollowings:(int64_t)pageDelay completion:(void (^)(NSArray<Profile*>* _Nullable, NSString* _Nullable))completion;

@end

@interface SwiftSocialApi : NSObject <SocialApi>

- (void)login:(NSString*)username password:(NSString*)password completion:(void (^)(BOOL, NSString* _Nullable))completion;
- (void)restoreSession:(void (^)(BOOL, NSString* _Nullable))completion;
- (void)fetchUserProfile:(void (^)(User* _Nullable, NSString* _Nullable))completion;
- (void)fetchFollowers:(int64_t)pageDelay completion:(void (^)(NSArray<Profile*>* _Nullable, NSString* _Nullable))completion;
- (void)fetchFollowings:(int64_t)pageDelay completion:(void (^)(NSArray<Profile*>* _Nullable, NSString* _Nullable))completion;

@end


