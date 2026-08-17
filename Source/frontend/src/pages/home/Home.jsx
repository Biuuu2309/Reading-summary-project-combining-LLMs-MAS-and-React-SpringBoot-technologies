import ParticlesBackground from '../../components/Particles';
import { MorphingNavigation } from "../../components/lightswind/morphing-navigation.tsx";
import { Home as HomeIcon, ShoppingBag, Info, HelpCircle, BookOpen, BookText } from "lucide-react";
import './Home.css';
import { useEffect, useState, useCallback, useMemo } from "react";
import { useNavigate, Link } from "react-router-dom";
import GradualBlur from '../../components/GradualBlur';
import RotatingText from '../../components/RotatingText';
import SplashCursor from '../../components/SplashCursor';
import StarBorder from '../../components/StarBorder';
import GradientText from '../../components/GradientText';
import ScrambledText from '../../components/ScrambledText';
import { GlowingCards, GlowingCard } from "../../components/lightswind/glowing-cards";
import { Zap, Sparkles, Crown } from "lucide-react";
import { AuroraTextEffect } from "../../components/lightswind/aurora-text-effect";
import ShinyText from '../../components/ShinyText';
import { InteractiveGradient } from "../../components/lightswind/interactive-gradient-card";
import ScrollList from '../../components/lightswind/scroll-list';
import MetaBalls from '../../components/MetaBalls';
import { PlusIcon } from 'lucide-react';
import { RippleButton, RippleButtonRipples } from '../../components/animate-ui/components/buttons/ripple';
import { getStoredUser, logout } from '../../services/authService';
import { User as UserIcon, LogOut } from 'lucide-react';

function throttle(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

// Function to generate random color
const generateRandomColor = () => {
  // Generate random RGB values for light/pastel colors
  const r = Math.floor(Math.random() * 100) + 150; // 150-250 (light colors)
  const g = Math.floor(Math.random() * 100) + 150;
  const b = Math.floor(Math.random() * 100) + 150;
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`;
};

// Function to generate a complementary/light color for cursor
const generateCursorColor = () => {
  // Return white or a very light color for cursor
  return '#ffffff';
};

export default function Home() {
  const [currentUser, setCurrentUser] = useState(() => getStoredUser());
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  useEffect(() => {
    const onStorage = () => setCurrentUser(getStoredUser());
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const handleLogout = useCallback(() => {
    logout();
    setCurrentUser(null);
    setIsUserMenuOpen(false);
  }, []);
  const navigate = useNavigate();
  const [isScrolled, setIsScrolled] = useState(false);
  const [isLogoHidden, setIsLogoHidden] = useState(false);

  // Generate random colors for MetaBalls (memoized to keep them consistent)
  const metaBallsColor = useMemo(() => generateRandomColor(), []);
  const metaBallsCursorColor = useMemo(() => generateCursorColor(), []);

  const handleScroll = useCallback(() => {
    setIsScrolled(window.scrollY > 20);
    setIsLogoHidden(window.scrollY > 50);
  }, []);

  useEffect(() => {
    const throttledScroll = throttle(handleScroll, 16); // ~60fps
    window.addEventListener('scroll', throttledScroll, { passive: true });
    handleScroll();
    return () => window.removeEventListener('scroll', throttledScroll);
  }, [handleScroll]);

  // Handle navigation link clicks
  const handleLinkClick = useCallback((link) => {
    if (link.id === 'summary') {
      navigate('/summary');
    } else if (link.id === 'story') {
      navigate('/story');
    } else if (link.id === 'mas-flow') {
      navigate('/mas-flow');
    }
    // Other links will use default scroll behavior handled by MorphingNavigation
  }, [navigate]);

  // Memoize links để tránh re-render không cần thiết
  const navLinks = useMemo(() => {
    const links = [
      { id: 'home', label: 'Home', href: '#home', icon: <HomeIcon size={30} /> },
      { id: 'summary', label: 'Summary', href: '/summary', icon: <BookOpen size={30} /> },
    ];
    if (currentUser?.userId) {
      links.push({ id: 'story', label: 'Story', href: '/story', icon: <BookText size={30} /> });
    }
    links.push({ id: 'mas-flow', label: 'MAS Flow', href: '/mas-flow', icon: <Zap size={30} /> });
    return links;
  }, [currentUser?.userId]);

  const learningSteps = useMemo(() => [
    { title: '1. Chọn bài đọc', description: 'Dán văn bản vào ô nhập liệu, hoặc tải file PDF/ảnh bài đọc từ sách giáo khoa.' },
    { title: '2. Chọn lớp & cách tóm tắt', description: 'Em chọn lớp mình đang học (1–5), chọn tóm tắt trích xuất hay diễn giải, và độ dài ngắn – trung bình – dài.' },
    { title: '3. MAS-VISUM đọc và hiểu', description: 'Đội ngũ trợ lý thông minh đọc bài, tìm ý quan trọng và chuẩn bị bản tóm tắt phù hợp với lớp em.' },
    { title: '4. Nhận bản tóm tắt dễ hiểu', description: 'Em đọc ngay trên màn hình — nội dung ngắn gọn, dùng từ vựng phù hợp, có thể kèm ảnh minh họa.' },
    { title: '5. Hỏi thêm hoặc đọc truyện', description: 'Em có thể chat hỏi thêm về bài đọc, hoặc sang trang Story để khám phá truyện thú vị!' },
  ], []);

  return (
    <div className="home-page" style={{ width: '100%', minHeight: '100vh', position: 'relative' }}>
      <div
        className="buttonNavigation"
        style={{
          display: 'flex',
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginRight: '10px',
          marginTop: '30px',
          gap: '0.75rem',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginLeft: '20px' }}>
        <img src="/images/logo.png" alt="" style={{ width: '80px', height: '70px' }} />
          <AuroraTextEffect
            text="MAS-VISUM"
            fontSize="clamp(2.5rem, 4.5vw, 3rem)"
            textClassName="home-brand-title"
            colors={{
              first: "bg-cyan-400",
              second: "bg-yellow-400",
              third: "bg-green-400",
              fourth: "bg-purple-500"
            }}
            blurAmount="blur-lg"
            className="aurora-text-effect ml-3"
          />
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginRight: '20px', position: 'relative' }}>
        {currentUser ? (
          <>
            <button
              type="button"
              onClick={() => setIsUserMenuOpen((v) => !v)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                padding: '8px 12px',
                background: 'rgba(255,255,255,0.95)',
                border: '1px solid rgba(0,0,0,0.1)',
                borderRadius: '12px',
                cursor: 'pointer',
                backdropFilter: 'blur(10px)',
              }}
              title={currentUser.role || 'User'}
            >
              {currentUser.avatarUrl ? (
                <img
                  src={currentUser.avatarUrl}
                  alt=""
                  style={{ width: '28px', height: '28px', borderRadius: '999px', objectFit: 'cover' }}
                />
              ) : (
                <div style={{ width: '28px', height: '28px', borderRadius: '999px', background: '#E5E7EB', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <UserIcon size={16} />
                </div>
              )}
              <span style={{ fontWeight: 600, color: '#374151', fontSize: '14px' }}>{currentUser.username}</span>
            </button>

            {isUserMenuOpen && (
              <div
                style={{
                  position: 'absolute',
                  top: '48px',
                  right: 0,
                  width: '220px',
                  background: 'rgba(255,255,255,0.98)',
                  border: '1px solid rgba(0,0,0,0.08)',
                  borderRadius: '12px',
                  boxShadow: '0 12px 24px rgba(0,0,0,0.12)',
                  padding: '8px',
                  zIndex: 1000,
                }}
              >
                <div style={{ padding: '8px 10px', color: '#6B7280', fontSize: '12px' }}>
                  Role: <b style={{ color: '#374151' }}>{currentUser.role || 'CHILD'}</b>
                </div>
                <button
                  type="button"
                  onClick={handleLogout}
                  style={{
                    width: '100%',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    padding: '10px',
                    border: 'none',
                    background: 'transparent',
                    borderRadius: '10px',
                    cursor: 'pointer',
                    color: '#EF4444',
                    fontWeight: 600,
                  }}
                >
                  <LogOut size={16} />
                  Đăng xuất
                </button>
              </div>
            )}
          </>
        ) : (
          <>
            <Link to="/login">
              <RippleButton variant="outline" size="lg" style={{ borderRadius: '10px', height: '40px' }}>
                <div style={{ fontFamily: "none", fontWeight: "bold" }}>Login</div>
                <RippleButtonRipples />
              </RippleButton>
            </Link>
            <Link to="/register">
              <RippleButton variant="default" size="lg" style={{ borderRadius: '10px', height: '40px', opacity: '0.8' }}>
                <div style={{ fontFamily: "none", fontWeight: "bold" }}>Register</div>
                <RippleButtonRipples />
              </RippleButton>
            </Link>
          </>
        )}
        </div>
      </div>
      {/* <SplashCursor /> */}
      <ParticlesBackground
        particleColors={['#00ffff', '#ff00ff', '#ffaa00', '#0000ff', '#00ff00', '#ff0000']}
        particleBaseSize={400}
        particleCount={80}
        particleSpread={15}
        speed={0.15}
        className="particles-bg"
      />
      {isScrolled && <div className="scroll-overlay" />}
      <MorphingNavigation
        links={navLinks}
        theme="custom"
        backgroundColor="#ffffff00"
        textColor="#60a5fa"
        borderColor="rgba(96, 165, 250, 0.55)"
        onLinkClick={handleLinkClick}
        scrollThreshold={150}
        animationDuration={1.5}
        enablePageBlur={true}
        glowIntensity={5}
        onMenuToggle={(isOpen) => console.log('Menu:', isOpen)}
      />


      <section style={{ position: 'relative', minHeight: '100vh' }}>
        <div style={{ padding: '1rem 2rem' }}>
          <div className="container-fluid" style={{ width: '90%', marginLeft: '5%', marginRight: '5%', marginTop: '2.5%', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', zIndex: 1, borderRadius: '25px' }}>
            <div className="container-fluid HOME1" style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0', flexDirection: 'row' }}>
              <div className="container" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%', display: 'flex', flexDirection: 'column' }}>
                <div className="container-fluid" style={{ display: 'flex', flexDirection: 'row', marginTop: '10%' }}>
                  <div className="container" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '50%', display: 'flex', alignItems: 'center', justifyContent: 'flex-end', marginRight: '10px' }}>
                    <h1 style={{ margin: '0', fontSize: '3.5rem', fontWeight: 'bold', color: '#7dcb88', fontFamily: 'Merriweather' }}>Vì lợi ích</h1>
                  </div>
                  <div className="container" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%', display: 'flex', justifyContent: 'flex-start', alignItems: 'center', fontFamily: 'Merriweather', fontSize: '3.5rem', fontStyle: 'italic', fontWeight: 'bold', color: '#55925d' }}>
                    <RotatingText
                      texts={['mười năm trồng cây', 'trăm năm trồng người']}
                      mainClassName="px-2 sm:px-2 md:px-3 overflow-hidden py-0.5 sm:py-1 md:py-3 justify-center rounded-lg"
                      staggerFrom={"first"}
                      initial={{ y: "100%" }}
                      animate={{ y: 0 }}
                      exit={{ y: "-120%" }}
                      staggerDuration={0.025}
                      splitLevelClassName="overflow-hidden pb-0.5 sm:pb-1 md:pb-1"
                      transition={{ type: "spring", damping: 30, stiffness: 400 }}
                      rotationInterval={5000}
                    />
                  </div>
                </div>
                <div className="container-fluid" style={{ display: 'flex', justifyContent: 'center', marginBottom: '5%' }}>
                  <h1 style={{ margin: '0', fontSize: '2.5rem', color: '#000000', fontFamily: 'Merriweather', fontStyle: 'italic' }}>Chắt lọc tri thức – Nuôi dưỡng hiểu biết.</h1>
                </div>
                <div className="container-fluid" style={{ display: 'flex', justifyContent: 'flex-start', marginLeft: '3%', width: 'auto', marginRight: '3%', marginBottom: '1%', textAlign: 'justify' }}>
                  <ScrambledText
                    className="scrambled-text-demo m-0"
                    radius={50}
                    duration={1.2}
                    speed={0.5}
                    scrambleChars=".:">
                    <div
                      style={{
                        margin: '0',
                        fontSize: '2rem',
                        color: '#000000',
                        fontFamily: 'Merriweather',
                        textAlign: 'justify',
                      }}
                    >
                      Phần mềm giúp học sinh tiếp cận nội dung bài học một cách nhẹ nhàng, dễ hiểu, từ đó nâng cao khả năng đọc hiểu và hình thành tư duy học hiểu ngay từ những năm đầu.
                    </div>
                  </ScrambledText>
                </div>
                <div className="container-fluid" style={{ display: 'flex', justifyContent: 'flex-start', marginLeft: '3%', width: 'auto', marginRight: '3%', marginBottom: '5%', textAlign: 'justify' }}>
                  <ScrambledText
                    className="scrambled-text-demo m-0"
                    radius={50}
                    duration={1.2}
                    speed={0.5}
                    scrambleChars=".:">
                    <div
                      style={{
                        margin: '0',
                        fontSize: '2rem',
                        color: '#000000',
                        fontFamily: 'Merriweather',
                        textAlign: 'justify',
                      }}
                    >
                      Hệ thống tích hợp trí tuệ nhân tạo hỗ trợ tóm tắt trích xuất và diễn giải truyện, văn bản, bài đọc tiếng Việt theo từng cấp lớp tiểu học.
                    </div>
                  </ScrambledText>
                </div>
                <div className="container-fluid divbtn" style={{ display: 'flex', justifyContent: 'flex-start', marginLeft: '3%', width: 'auto', marginRight: '3%', marginBottom: '10%' }}>
                  <StarBorder
                    as="button"
                    className="custom-class"
                    color="#940cd5"
                    speed="3s"
                    onClick={() => navigate('/summary')}>
                    <GradientText
                      colors={["#ff3838", "#1eb528", "#21bbca", "#ad38ff", "#ff38c1"]}
                      animationSpeed={8}
                      showBorder={false}
                      className="custom-class">
                      <h3 style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'center' }}>Bắt đầu ngay
                        <img src="/images/next-button.png" alt="" style={{ height: '30px', marginLeft: '10px', marginTop: '4px' }} />
                      </h3>
                    </GradientText>
                  </StarBorder>
                </div>
              </div>
              <div className="container" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%' }}>
                <img
                  src="/images/image-thumnail.png"
                  style={{ opacity: "0.9", willChange: 'opacity', transform: 'translateZ(0)' }}
                  alt=""
                  loading="lazy"
                />
              </div>
            </div>
            <div className="container-fluid HOME2 home-kids-section" style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0', flexDirection: 'column' }}>
              <div className="container home-kids-section-title">
                <ShinyText
                  text="MAS-VISUM giúp em gì?"
                  speed={3.3}
                  delay={0}
                  color="#eb6770"
                  shineColor="#fad784"
                  spread={120}
                  direction="left"
                  yoyo={false}
                  pauseOnHover={false}
                  disabled={false}
                />
              </div>
              <div className="container-fluid" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: '2%' }}>
                <GlowingCards
                  enableGlow={true}
                  glowRadius={30}
                  glowOpacity={0.8}
                  animationDuration={500}
                  gap="3rem"
                  responsive={true}
                >
                  <GlowingCard glowColor="#eb3743" className="space-y-5">
                    <InteractiveGradient
                      color="#ffc8a4"
                      glowColor="#eb6770"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ffe4d2"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/problem.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#df8144' }}>Đọc bài dài, hiểu nhanh hơn</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#1890ff"
                      glowColor="#eb6770"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ffe4d2"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        Bài đọc dài khiến em mệt và khó nhớ hết ý chính? MAS-VISUM rút gọn lại, giữ những điều quan trọng nhất để em đọc dễ hơn.
                        <br /><br />
                        Em vẫn hiểu đúng nội dung bài — chỉ ngắn gọn và nhẹ nhàng hơn thôi!
                      </div>
                    </InteractiveGradient>
                    </GlowingCard>
                  <GlowingCard glowColor="#2c2c2c" className="space-y-4">
                  <InteractiveGradient
                      color="#8d2799"
                      glowColor="#ffffff"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#f3e8ff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/target.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#7c3aed' }}>Phù hợp từng lớp (1–5)</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#8d2799"
                      glowColor="#ffffff"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#faf5ff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        Lớp 1 dùng từ đơn giản, lớp 5 phong phú hơn — MAS-VISUM tự điều chỉnh theo lớp em chọn.
                        <br /><br />
                        Bản tóm tắt luôn dùng từ vựng em có thể hiểu được, giúp em tự tin đọc và học tốt hơn mỗi ngày.
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                  <GlowingCard glowColor="#ffcc33" className="space-y-4">
                    <InteractiveGradient
                      color="#661010"
                      glowColor="#ffbf00"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ffe8a4"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/energy-saving-light.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#a57d05' }}>Hai cách tóm tắt hay ho</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#661010"
                      glowColor="#ffbf00"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ffe8a4"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        <strong>Trích xuất:</strong> giữ lại những câu hay nhất trong bài, đúng như sách viết.
                        <br /><br />
                        <strong>Diễn giải:</strong> kể lại bài đọc bằng lời dễ hiểu — như em tóm tắt cho bạn nghe!
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                </GlowingCards>
              </div>
              <div className="container-fluid" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginTop: '1%', marginBottom: '3%' }}>
                <GlowingCards
                  enableGlow={true}
                  glowRadius={30}
                  glowOpacity={0.8}
                  animationDuration={500}
                  gap="3rem"
                  responsive={true}
                >
                  <GlowingCard glowColor="#2d7cff" className="space-y-4">
                    <InteractiveGradient
                      color="#2d7cff"
                      glowColor="#41d1ff"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#d9ebff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/connection.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#2d5fa8' }}>Đội ngũ trợ lý thông minh</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#2d7cff"
                      glowColor="#41d1ff"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#edf5ff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        MAS-VISUM có nhiều “bạn robot nhỏ” cùng làm việc: hiểu yêu cầu của em, đọc bài, tóm tắt và kiểm tra lại kết quả.
                        <br /><br />
                        Mỗi bạn có một nhiệm vụ riêng — giống như một đội bạn cùng nhau giúp em học tốt hơn!
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>

                  <GlowingCard glowColor="#22a06b" className="space-y-4">
                    <InteractiveGradient
                      color="#22a06b"
                      glowColor="#99f2c8"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#d6f7e7"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/predictive.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#21714f' }}>Tóm tắt trích xuất</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#22a06b"
                      glowColor="#99f2c8"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ebfff5"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        MAS-VISUM chọn những câu quan trọng nhất trong bài và ghép lại thành bản tóm tắt ngắn.
                        <br /><br />
                        Lời văn giữ nguyên như trong sách — em đọc đúng nội dung gốc, không bị thay đổi ý nghĩa.
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>

                  <GlowingCard glowColor="#b04bff" className="space-y-4">
                    <InteractiveGradient
                      color="#b04bff"
                      glowColor="#ff9ad5"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#f2ddff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-3 text-white rounded-xl home-kids-card-header">
                        <img src="/images/knowledge.png" alt="" className="home-kids-card-icon" />
                        <h3 className="home-kids-card-title" style={{ color: '#7e35b4' }}>Tóm tắt diễn giải</h3>
                      </div>
                    </InteractiveGradient>
                    <InteractiveGradient
                      color="#b04bff"
                      glowColor="#ff9ad5"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#f9eeff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="p-2 home-kids-card-body">
                        MAS-VISUM đọc hiểu cả bài rồi viết lại ngắn gọn bằng lời dễ hiểu — mạch lạc như em kể chuyện cho mẹ nghe.
                        <br /><br />
                        Em chọn độ dài ngắn, trung bình hay dài — tùy theo em muốn đọc nhanh hay đọc kỹ hơn!
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                </GlowingCards>
              </div>
            </div>
            <div className="container-fluid HOME3 home-kids-section" style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0', flexDirection: 'column' }}>
              <div className="container home-kids-steps-layout">
                <div style={{ position: 'absolute', width: '100%', height: '100%', zIndex: 1, marginRight: '70%' }}>
                  <MetaBalls
                    color={metaBallsColor}
                    cursorBallColor={metaBallsCursorColor}
                    cursorBallSize={2}
                    ballCount={15}
                    animationSize={25}
                    enableMouseInteraction
                    enableTransparency={true}
                    hoverSmoothness={0.15}
                    clumpFactor={1}
                    speed={0.3}
                  />
                </div>
                <div style={{ position: 'relative', zIndex: 2, width: '38%' }}>
                  <ShinyText
                    text="Học cùng MAS-VISUM trong 5 bước"
                    speed={3.3}
                    delay={0}
                    color="#f9a967"
                    shineColor="#ff55da"
                    spread={100}
                    direction="left"
                    yoyo={false}
                    pauseOnHover={false}
                    disabled={false}
                  />
                </div>
                <div style={{ position: 'relative', zIndex: 2 }}>
                  <ScrollList
                    data={learningSteps}
                    renderItem={(item) => (
                      <div>
                        <h3 className="home-kids-scroll-title">{item.title}</h3>
                        <p className="home-kids-scroll-desc">{item.description}</p>
                      </div>
                    )}
                    itemSpacing={24}
                  />
                  <div className="home-kids-scroll-hint">
                    <ShinyText
                      text="Cuộn xuống để xem thêm"
                      speed={3.3}
                      delay={0}
                      color="#1e3a5f"
                      shineColor="#ffffff"
                      spread={120}
                      direction="left"
                      yoyo={false}
                      pauseOnHover={false}
                      disabled={false}
                    />
                    <img src="/images/arrow.png" alt="" style={{ width: '24px', height: '24px', marginTop: '8px' }} />
                  </div>
                </div>
              </div>
            </div>
            <div className="container-fluid HOME4 home-kids-section" style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0', flexDirection: 'column' }}>
              <div className="container-fluid" style={{ padding: '0', backgroundColor: 'transparent', height: 'auto', width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '3%', flexDirection: 'column' }}>
                <InteractiveGradient
                  color="#661010"
                  glowColor="#ff8080"
                  followMouse={true}
                  hoverOnly={false}
                  intensity={50}
                  backgroundColor="transparent"
                  width="100%"
                  height="100%"
                  borderRadius="1.5rem 1.5rem 0 0"
                >
                  <div className="p-3 text-white rounded-xl home-kids-cta-header">
                    <Sparkles size={48} color="#f9a967" style={{ marginRight: '16px', flexShrink: 0 }} />
                    <ShinyText
                      text="Cùng MAS-VISUM học mỗi ngày!"
                      speed={3.3}
                      delay={0}
                      color="#f9a967"
                      shineColor="#ff55da"
                      spread={50}
                      direction="left"
                      yoyo={false}
                      pauseOnHover={false}
                      disabled={false}
                    />
                  </div>
                </InteractiveGradient>
                <div className="home-kids-badges" style={{ marginTop: '40px', width: '100%' }}>
                <GlowingCards
                  enableGlow={true}
                  glowRadius={30}
                  glowOpacity={0.8}
                  animationDuration={500}
                  gap="2rem"
                  responsive={true}
                >
                  <GlowingCard glowColor="#22a06b" className="space-y-4">
                    <InteractiveGradient
                      color="#22a06b"
                      glowColor="#99f2c8"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#ebfff5"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="home-kids-badge">
                        <BookOpen size={40} color="#21714f" />
                        <h3 className="home-kids-badge-title">Đọc hiểu tốt hơn</h3>
                        <p className="home-kids-badge-desc">Bài dài trở nên ngắn gọn, em nắm ý chính nhanh và nhớ lâu hơn.</p>
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                  <GlowingCard glowColor="#2d7cff" className="space-y-4">
                    <InteractiveGradient
                      color="#2d7cff"
                      glowColor="#41d1ff"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#edf5ff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="home-kids-badge">
                        <Crown size={40} color="#2d5fa8" />
                        <h3 className="home-kids-badge-title">Phù hợp từng lớp</h3>
                        <p className="home-kids-badge-desc">Từ vựng và độ dài được điều chỉnh theo lớp 1 đến lớp 5.</p>
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                  <GlowingCard glowColor="#b04bff" className="space-y-4">
                    <InteractiveGradient
                      color="#b04bff"
                      glowColor="#ff9ad5"
                      followMouse={true}
                      hoverOnly={false}
                      intensity={100}
                      backgroundColor="#f9eeff"
                      width="100%"
                      height="100%"
                      borderRadius="1.5rem"
                    >
                      <div className="home-kids-badge">
                        <Sparkles size={40} color="#7e35b4" />
                        <h3 className="home-kids-badge-title">Vui và dễ dùng</h3>
                        <p className="home-kids-badge-desc">Giao diện đẹp, thao tác đơn giản — em tự khám phá và học cùng bạn bè.</p>
                      </div>
                    </InteractiveGradient>
                  </GlowingCard>
                </GlowingCards>
                </div>
                <div className="home-kids-cta-btn">
                  <StarBorder
                    as="button"
                    className="custom-class"
                    color="#940cd5"
                    speed="3s"
                    onClick={() => navigate('/summary')}>
                    <GradientText
                      colors={["#ff3838", "#1eb528", "#21bbca", "#ad38ff", "#ff38c1"]}
                      animationSpeed={8}
                      showBorder={false}
                      className="custom-class">
                      <h3 style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'center', margin: 0 }}>
                        Thử tóm tắt ngay
                        <img src="/images/next-button.png" alt="" style={{ height: '30px', marginLeft: '10px', marginTop: '4px' }} />
                      </h3>
                    </GradientText>
                  </StarBorder>
                </div>
              </div>
            </div>
          </div>
        </div>
        <GradualBlur
          target="page"        
          position="bottom"
          height="7rem"
          strength={2}
          divCount={5}
          curve="bezier"
          exponential
          opacity={1}
        />
      </section>
    </div>
  );
}
