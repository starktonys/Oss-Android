# 阿里云对象存储服务（Object Storage Service，简称 OSS） #

### 接入步骤
- Application中初始化

`OssService.getInstance().init(this);`

- 登录app后初始化oss配置信息

		public void init(Context context, String accessKeyId, String accessKeySecret, String endpoint, String bucketAddress, String bucket) {
		writeToLocal(accessKeyId, accessKeySecret,  endpoint,  bucketAddress,  bucket);
		mCredentialProvider = new OSSCustomSignerCredentialProvider() {
		@Override
		public String signContent(String content) {
		return OSSUtils.sign(AccessKeyId, AccessKeySecret, content);
		}
		};
		mOSSClient = new OSSClient(context, Endpoint, mCredentialProvider);
		}
 

- 异步上传图片

		OssService.getInstance().asyncUploadImage(FolderUtils.getFolderPath(file.getName(), Constant.FOLDER_USER), file.toString(), HashMapUtil.initUploadImageParams("type"), new OSSCompletedCallback() {
		            @Override
		            public void onSuccess(OSSRequest request, OSSResult result, Map extraData) {
		
		            }
		
		            @Override
		            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
		
		            }
		
		            @Override
		            public void onProgress(OSSRequest request, long currentSize, long totalSize) {
		
		            }
		        });

- 异步上传图片返回宽高额外信息

		OssService.getInstance().asyncUploadImageWithWH(FolderUtils.getFolderPath(file.getName(), Constant.FOLDER_USER), file.toString(), HashMapUtil.initUploadImageParams("type"), new OSSCompletedCallback() {
		            @Override
		            public void onSuccess(OSSRequest request, OSSResult result, Map extraData) {
		
		            }
		
		            @Override
		            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
		
		            }
		
		            @Override
		            public void onProgress(OSSRequest request, long currentSize, long totalSize) {
		
		            }
		        });



- 异步断点续传上传视频

		OssService.getInstance().asyncUploadVideo(FolderUtils.getFolderPath(file.getName(),Constant.FOLDER_USER), file.toString(), HashMapUtil.initUploadImageParams(""), new OSSCompletedCallback() {
		            @Override
		            public void onSuccess(OSSRequest request, OSSResult result, Map extraData) {
		
		            }
		
		            @Override
		            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
		
		            }
		
		            @Override
		            public void onProgress(OSSRequest request, long currentSize, long totalSize) {
		
		            }
		        });

### [阿里云oss官方文档](https://help.aliyun.com/document_detail/32042.html?spm=a2c4g.11174283.6.941.7f487da24vy37L "阿里云oss官方文档")







